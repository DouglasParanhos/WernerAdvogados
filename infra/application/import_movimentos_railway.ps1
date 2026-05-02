# Importa movimentos (docs/movimentos-relatorio-processos.md) no banco de produção Railway
# Requer: railway login (execute em terminal interativo primeiro)
#
# Uso:
#   .\infra\application\import_movimentos_railway.ps1
#
# Ou manualmente:
#   npx -y @railway/cli run --service Postgres python scripts/import_movimentos_md_to_db.py --md docs/movimentos-relatorio-processos.md

$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent (Split-Path -Parent (Split-Path -Parent $PSScriptRoot))
Set-Location $ProjectRoot

$mdPath = "docs/movimentos-relatorio-processos.md"
if (-not (Test-Path $mdPath)) {
    Write-Host "Erro: $mdPath nao encontrado." -ForegroundColor Red
    exit 1
}

Write-Host "Importando movimentos no Railway (producao)..." -ForegroundColor Cyan
$result = npx -y @railway/cli run --service Postgres python scripts/import_movimentos_md_to_db.py --md $mdPath 2>&1
$LASTEXITCODE = $LASTEXITCODE
Write-Host $result
if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Falha na importacao. Verifique:" -ForegroundColor Yellow
    Write-Host "  1. Execute 'npx -y @railway/cli login' em um terminal interativo" -ForegroundColor Gray
    Write-Host "  2. Execute 'npx -y @railway/cli link' para vincular ao projeto" -ForegroundColor Gray
    exit 1
}
Write-Host ""
Write-Host "Importacao concluida." -ForegroundColor Green
