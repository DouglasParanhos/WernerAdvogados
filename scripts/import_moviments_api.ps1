<#
.SYNOPSIS
  Importa movimentações em lote via POST /api/moviments a partir do JSON gerado (movimentos_*_processos_tj_rj.json).

.DESCRIPTION
  - Autentica em /api/auth/login (usuário interno, não CLIENT) ou usa -BearerToken.
  - Se processId estiver vazio no JSON, tenta resolver pelo número CNJ em GET /api/processes?numero=...
  - Opcional: arquivo de mapa CNJ -> id (process_id_map.json).

.PARAMETER ApiBaseUrl
  Base da API (sem barra final). Padrão: $env:WA_API_BASE_URL ou http://localhost:8080

.PARAMETER JsonPath
  Caminho do JSON de movimentos. Padrão: movimentos_14_processos_tj_rj.json ao lado deste script.

.PARAMETER BearerToken
  JWT já obtido. Pode usar $env:WA_API_TOKEN.

.PARAMETER Username / Password
  Credenciais para login. Variáveis: WA_API_USERNAME e WA_API_PASSWORD (texto).

.PARAMETER ProcessIdMapPath
  JSON opcional: { "0059197-75.2023.8.19.0000": 123, ... }

.PARAMETER DryRun
  Apenas lista o que seria enviado, sem POST. Nao chama a API para resolver CNJ (use JSON ou mapa). Para simular resolucao via API, use tambem -DryRunResolve.

.PARAMETER DryRunResolve
  Com -DryRun, chama GET /api/processes?numero= para obter process_id (precisa backend acessivel).

.EXAMPLE
  .\import_moviments_api.ps1 -Username admin -Password (Read-Host -AsSecureString)

.EXAMPLE
  $env:WA_API_TOKEN = "eyJ..."
  .\import_moviments_api.ps1 -DryRun
#>
[CmdletBinding()]
param(
    [string] $ApiBaseUrl = $env:WA_API_BASE_URL,
    [string] $JsonPath = "",
    [string] $BearerToken = $env:WA_API_TOKEN,
    [string] $Username = $env:WA_API_USERNAME,
    [string] $PasswordPlain = $env:WA_API_PASSWORD,
    [string] $ProcessIdMapPath = "",
    [switch] $DryRun,
    [switch] $DryRunResolve,
    [switch] $ContinueOnError
)

$ErrorActionPreference = "Stop"

if (-not $ApiBaseUrl) { $ApiBaseUrl = "http://localhost:8080" }
$ApiBaseUrl = $ApiBaseUrl.TrimEnd("/")

if (-not $JsonPath) {
    $JsonPath = Join-Path $PSScriptRoot "movimentos_14_processos_tj_rj.json"
}

if (-not (Test-Path -LiteralPath $JsonPath)) {
    Write-Error "Arquivo JSON não encontrado: $JsonPath"
}

$raw = Get-Content -LiteralPath $JsonPath -Raw -Encoding UTF8
$data = $raw | ConvertFrom-Json

$headers = @{
    "Content-Type" = "application/json; charset=utf-8"
}

function Get-BearerTokenFromLogin {
    param([string] $Base, [string] $User, [string] $Pass)
    if (-not $User -or -not $Pass) {
        Write-Error "Informe -Username e -Password (ou WA_API_USERNAME / WA_API_PASSWORD) ou -BearerToken / WA_API_TOKEN."
    }
    $loginBody = @{ username = $User; password = $Pass } | ConvertTo-Json -Compress
    try {
        $resp = Invoke-RestMethod -Uri "$Base/api/auth/login" -Method Post -Body $loginBody -Headers @{ "Content-Type" = "application/json" }
    } catch {
        Write-Error "Falha no login: $($_.Exception.Message)"
    }
    if (-not $resp.token) {
        Write-Error "Resposta de login sem token."
    }
    return $resp.token
}

$map = $null
if ($ProcessIdMapPath -and (Test-Path -LiteralPath $ProcessIdMapPath)) {
    $map = (Get-Content -LiteralPath $ProcessIdMapPath -Raw -Encoding UTF8 | ConvertFrom-Json)
}

if (-not $BearerToken) {
    Write-Host "Obtendo token via /api/auth/login..."
    $BearerToken = Get-BearerTokenFromLogin -Base $ApiBaseUrl -User $Username -Pass $PasswordPlain
}

$headers["Authorization"] = "Bearer $BearerToken"

function Resolve-ProcessId {
    param(
        [string] $Base,
        [hashtable] $Hdr,
        [string] $Cnj,
        [long] $ExplicitId,
        $MapObj
    )
    if ($null -ne $ExplicitId -and [long]$ExplicitId -gt 0) { return [long]$ExplicitId }
    if ($MapObj -and $Cnj) {
        $mv = $MapObj.$Cnj
        if ($null -ne $mv -and [long]$mv -gt 0) { return [long]$mv }
    }
    $enc = [System.Uri]::EscapeDataString($Cnj)
    $uri = "$Base/api/processes?page=0&size=20&numero=$enc"
    try {
        $page = Invoke-RestMethod -Uri $uri -Method Get -Headers $Hdr
    } catch {
        Write-Warning "Não foi possível buscar processo pelo número '$Cnj': $($_.Exception.Message)"
        return $null
    }
    $list = $page.content
    if (-not $list) { $list = @() }
    $exact = @($list | Where-Object { $_.numero -eq $Cnj })
    if ($exact.Count -eq 1) { return [long]$exact[0].id }
    if ($exact.Count -gt 1) {
        Write-Warning "Vários processos com o mesmo número '$Cnj'; use processId no JSON ou no mapa."
        return $null
    }
    if ($list.Count -ge 1) {
        $first = $list[0]
        if ($first.numero -eq $Cnj) { return [long]$first.id }
    }
    Write-Warning "Processo não encontrado na API para número: $Cnj"
    return $null
}

$stats = @{ ok = 0; fail = 0; skipProcess = 0; dryRun = 0 }

foreach ($proc in $data.processos) {
    $cnj = $proc.numero_cnj
    $procId = $null
    if ($null -ne $proc.processId) {
        try { $procId = [long]$proc.processId } catch { $procId = $null }
    }
    if ($DryRun -and -not $DryRunResolve) {
        if (($null -eq $procId -or [long]$procId -le 0) -and $map -and $cnj) {
            $mv = $map.$cnj
            if ($null -ne $mv -and [long]$mv -gt 0) { $procId = [long]$mv }
        }
    }
    else {
        $procId = Resolve-ProcessId -Base $ApiBaseUrl -Hdr $headers -Cnj $cnj -ExplicitId $procId -MapObj $map
    }
    if (-not $procId) {
        Write-Warning "Ignorando processo indice $($proc.indice) CNJ $cnj - sem processId."
        $stats.skipProcess++
        continue
    }

    Write-Host "`n=== Processo $($proc.indice) | CNJ $cnj | process_id=$procId ===" -ForegroundColor Cyan

    foreach ($m in @($proc.movimentos_cronologicos)) {
        $bodyObj = @{
            descricao       = $m.descricao
            date            = $m.date_iso
            processId       = $procId
            visibleToClient = $true
        }
        $jsonBody = $bodyObj | ConvertTo-Json -Compress -Depth 5

        if ($DryRun) {
            $snip = if ($m.descricao.Length -gt 60) { $m.descricao.Substring(0, 60) + "..." } else { $m.descricao }
            Write-Host "[DRY-RUN] POST /api/moviments $($m.date_iso) $snip"
            $stats.dryRun++
            continue
        }

        try {
            $null = Invoke-RestMethod -Uri "$ApiBaseUrl/api/moviments" -Method Post -Body $jsonBody -Headers $headers -ContentType "application/json; charset=utf-8"
            Write-Host "  OK $($m.date_iso)"
            $stats.ok++
        } catch {
            Write-Warning "  FALHA $($m.date_iso): $($_.Exception.Message)"
            $stats.fail++
            if (-not $ContinueOnError) {
                throw
            }
        }
    }
}

if ($DryRun) {
    $msg = "`nResumo dry-run: linhas que seriam enviadas=$($stats.dryRun) | processos ignorados sem ID=$($stats.skipProcess)"
    Write-Host $msg -ForegroundColor Yellow
}
else {
    $msg = "`nResumo: movimentos criados=$($stats.ok) | falhas POST=$($stats.fail) | processos ignorados sem ID=$($stats.skipProcess)"
    Write-Host $msg -ForegroundColor Green
}
if ($stats.fail -gt 0) { exit 1 }
