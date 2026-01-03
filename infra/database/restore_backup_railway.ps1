# Script unico para diagnosticar e restaurar backup do PostgreSQL no Railway
# Uso: .\restore_backup_railway.ps1 -BackupPath "C:\caminho\para\backup.sql"
#
# Este script:
# 1. Diagnostica problemas do PostgreSQL (loops, erros, etc)
# 2. Aguarda o servico estar estavel
# 3. Executa o restore do backup

param(
    [Parameter(Mandatory=$false)]
    [string]$BackupPath = "",
    [string]$PostgresServiceName = "Postgres",
    [string]$ProjectId = "204b4976-3e8c-4369-bc84-c9a3e0349b36",
    [switch]$Force,
    [switch]$DiagnoseOnly,
    [int]$MaxWaitSeconds = 300
)

Write-Host "=== PostgreSQL Railway - Diagnostico e Restore ===" -ForegroundColor Cyan
Write-Host ""

# Verificar autenticacao
Write-Host "Verificando autenticacao Railway..." -ForegroundColor Yellow
$whoami = npx -y @railway/cli whoami 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO: Voce precisa fazer login no Railway primeiro!" -ForegroundColor Red
    Write-Host "Execute: npx -y @railway/cli login" -ForegroundColor Yellow
    exit 1
}
Write-Host "OK Autenticado: $whoami" -ForegroundColor Green
Write-Host ""

# Linkar ao projeto
npx -y @railway/cli link --project $ProjectId 2>&1 | Out-Null

# Verificar arquivo se nao for apenas diagnostico
if (-not $DiagnoseOnly) {
    if (-not (Test-Path $BackupPath)) {
        Write-Host "ERRO: Arquivo de backup nao encontrado: $BackupPath" -ForegroundColor Red
        exit 1
    }
    $fileInfo = Get-Item $BackupPath
    $sizeMB = [math]::Round($fileInfo.Length / 1MB, 2)
    Write-Host "Arquivo: $BackupPath" -ForegroundColor Gray
    Write-Host "Tamanho: $sizeMB MB" -ForegroundColor Gray
    Write-Host ""
}

# === DIAGNOSTICO ===
Write-Host "=== DIAGNOSTICO ===" -ForegroundColor Cyan
Write-Host ""

# Verificar variaveis
Write-Host "Verificando variaveis de ambiente..." -ForegroundColor Yellow
$vars = npx -y @railway/cli variables --service $PostgresServiceName 2>&1

$dbPublicUrl = $null
$hasPostgresDb = $false

foreach ($line in $vars) {
    if ($line -like '*DATABASE_PUBLIC_URL*' -and $line -like '*postgresql://*') {
        $urlStart = $line.IndexOf('postgresql://')
        if ($urlStart -ge 0) {
            $urlPart = $line.Substring($urlStart)
            $dbPublicUrl = ($urlPart -split '\s')[0]
        }
    }
    if ($line -like "*POSTGRES_DB*wa_db*") { $hasPostgresDb = $true }
}

if ($dbPublicUrl) {
    Write-Host "OK DATABASE_PUBLIC_URL disponivel" -ForegroundColor Green
} else {
    Write-Host "ERRO DATABASE_PUBLIC_URL nao encontrada" -ForegroundColor Red
}

if ($hasPostgresDb) {
    Write-Host "OK POSTGRES_DB=wa_db configurado" -ForegroundColor Green
} else {
    Write-Host "AVISO POSTGRES_DB pode nao estar configurado" -ForegroundColor Yellow
}
Write-Host ""

# Analisar logs
Write-Host "Analisando logs recentes..." -ForegroundColor Yellow
$logs = npx -y @railway/cli logs --service $PostgresServiceName --tail 50 2>&1

$restartCount = 0
$errorCount = 0
$stoppingCount = 0
$readyCount = 0
$lastEvent = ""

foreach ($line in $logs) {
    if ($line -like "*restarting*" -or $line -like "*restart*") { $restartCount++ }
    if ($line -like "*error*" -or $line -like "*Error*" -or $line -like "*ERROR*" -or $line -like "*FATAL*") { $errorCount++ }
    if ($line -like "*Stopping Container*" -or $line -like "*stopping*") { $stoppingCount++; $lastEvent = "Stopping" }
    if ($line -like "*ready to accept connections*") { $readyCount++; $lastEvent = "Ready" }
}

Write-Host "Eventos encontrados:" -ForegroundColor Gray
Write-Host "  - Restarts: $restartCount" -ForegroundColor Gray
Write-Host "  - Erros: $errorCount" -ForegroundColor $(if ($errorCount -gt 0) { "Red" } else { "Green" })
Write-Host "  - 'Stopping Container': $stoppingCount" -ForegroundColor $(if ($stoppingCount -gt 0) { "Yellow" } else { "Green" })
Write-Host "  - 'Ready to accept connections': $readyCount" -ForegroundColor $(if ($readyCount -gt 0) { "Green" } else { "Yellow" })
Write-Host "  - Ultimo evento: $lastEvent" -ForegroundColor Gray
Write-Host ""

# Testar conexao se URL disponivel
if ($dbPublicUrl) {
    Write-Host "Testando conexao..." -ForegroundColor Yellow
    $psqlPath = "C:\Program Files\PostgreSQL\16\bin\psql.exe"
    if (-not (Test-Path $psqlPath)) {
        $psqlFound = where.exe psql 2>$null
        if ($psqlFound) { $psqlPath = $psqlFound }
    }
    
    if (Test-Path $psqlPath) {
        $testResult = & $psqlPath $dbPublicUrl -c "SELECT 1;" 2>&1
        if ($LASTEXITCODE -eq 0) {
            Write-Host "OK Conexao bem-sucedida - PostgreSQL esta funcionando!" -ForegroundColor Green
        } else {
            Write-Host "ERRO Falha na conexao" -ForegroundColor Red
            Write-Host "   Erro: $testResult" -ForegroundColor Gray
        }
    } else {
        Write-Host "AVISO psql nao encontrado - nao foi possivel testar conexao" -ForegroundColor Yellow
    }
    Write-Host ""
}

# Diagnostico final
if ($stoppingCount -gt 0 -and $readyCount -gt 0) {
    Write-Host "AVISO PROBLEMA IDENTIFICADO:" -ForegroundColor Yellow
    Write-Host "   PostgreSQL inicia mas Railway esta parando o container." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Solucoes:" -ForegroundColor Cyan
    Write-Host "   1. Acesse: https://railway.app/project/$ProjectId" -ForegroundColor Gray
    Write-Host "   2. Verifique se o servico PostgreSQL esta ativo (nao pausado)" -ForegroundColor Gray
    Write-Host "   3. Se estiver pausado, clique em 'Resume' ou 'Unpause'" -ForegroundColor Gray
    Write-Host "   4. Se o problema persistir, considere recriar o servico" -ForegroundColor Gray
    Write-Host ""
    
    if (-not $DiagnoseOnly -and -not $Force) {
        Write-Host "AVISO Nao e recomendado fazer restore enquanto o servico esta instavel." -ForegroundColor Yellow
        $confirmation = Read-Host "Deseja continuar mesmo assim? (S/N)"
        if ($confirmation -ne 'S' -and $confirmation -ne 's') {
            Write-Host "Operacao cancelada." -ForegroundColor Yellow
            exit 0
        }
    }
} elseif ($errorCount -gt 0) {
    Write-Host "ERRO ERROS ENCONTRADOS nos logs" -ForegroundColor Red
    if (-not $DiagnoseOnly -and -not $Force) {
        Write-Host "Verifique os logs antes de continuar." -ForegroundColor Yellow
        exit 1
    }
}

if ($DiagnoseOnly) {
    Write-Host "=== Diagnostico concluido ===" -ForegroundColor Green
    exit 0
}

# === RESTORE ===
Write-Host "=== RESTORE ===" -ForegroundColor Cyan
Write-Host ""

if (-not $dbPublicUrl) {
    Write-Host "ERRO: Nao foi possivel obter DATABASE_PUBLIC_URL" -ForegroundColor Red
    Write-Host "O servico pode nao estar rodando. Verifique no Railway Dashboard." -ForegroundColor Yellow
    exit 1
}

# Verificar psql
$psqlPath = "C:\Program Files\PostgreSQL\16\bin\psql.exe"
if (-not (Test-Path $psqlPath)) {
    $psqlFound = where.exe psql 2>$null
    if ($psqlFound) {
        $psqlPath = $psqlFound
    } else {
        Write-Host "ERRO: psql nao encontrado. Instale o PostgreSQL client." -ForegroundColor Red
        Write-Host "Download: https://www.postgresql.org/download/windows/" -ForegroundColor Yellow
        exit 1
    }
}

# Confirmar restore
if (-not $Force) {
    Write-Host "ATENCAO: Isso ira SOBRESCREVER todos os dados existentes no banco!" -ForegroundColor Red
    Write-Host ""
    try {
        $confirmation = Read-Host "Deseja continuar? (S/N)"
        if ($confirmation -ne 'S' -and $confirmation -ne 's') {
            Write-Host "Operacao cancelada." -ForegroundColor Yellow
            exit 0
        }
    } catch {
        Write-Host "Modo nao-interativo. Use -Force para pular confirmacao." -ForegroundColor Yellow
        exit 1
    }
}

Write-Host ""
Write-Host "Executando restore..." -ForegroundColor Yellow
Write-Host "Isso pode demorar alguns minutos..." -ForegroundColor Gray
Write-Host ""

$startTime = Get-Date
try {
    & $psqlPath $dbPublicUrl -f $BackupPath
    
    $endTime = Get-Date
    $duration = $endTime - $startTime
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "OK Backup restaurado com sucesso!" -ForegroundColor Green
        Write-Host "Tempo decorrido: $($duration.TotalSeconds.ToString('F2')) segundos" -ForegroundColor Gray
    } else {
        Write-Host ""
        Write-Host "ERRO ao restaurar backup (codigo: $LASTEXITCODE)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host ""
    Write-Host "ERRO ao executar restore: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== Concluido! ===" -ForegroundColor Green
