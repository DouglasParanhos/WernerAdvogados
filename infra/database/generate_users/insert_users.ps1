# Script PowerShell para inserir usuários no banco de dados
# Gera os hashes BCrypt e executa o SQL no container PostgreSQL

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Script de Inserção de Usuários" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verifica se o container está rodando
$containerRunning = docker ps --filter "name=wa-postgres" --format "{{.Names}}" | Select-String "wa-postgres"
if (-not $containerRunning) {
    Write-Host "Erro: Container wa-postgres não está rodando!" -ForegroundColor Red
    Write-Host "Execute: docker-compose up -d" -ForegroundColor Yellow
    exit 1
}

# Obtém o diretório do script
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Push-Location $scriptDir

Write-Host "Gerando hashes BCrypt e criando script SQL..." -ForegroundColor Cyan

# Verifica se o Python está disponível
if (Get-Command python3 -ErrorAction SilentlyContinue) {
    python3 generate_users.py | Out-File -FilePath insert_users.sql -Encoding utf8
} elseif (Get-Command python -ErrorAction SilentlyContinue) {
    python generate_users.py | Out-File -FilePath insert_users.sql -Encoding utf8
} else {
    Write-Host "Erro: Python não encontrado. Instale Python e a biblioteca bcrypt:" -ForegroundColor Red
    Write-Host "pip install bcrypt" -ForegroundColor Yellow
    Pop-Location
    exit 1
}

Write-Host "Script SQL gerado: insert_users.sql" -ForegroundColor Green
Write-Host ""
Write-Host "Executando no banco de dados..." -ForegroundColor Cyan

# Executa o SQL no container PostgreSQL
Get-Content insert_users.sql | docker exec -i wa-postgres psql -U postgres -d wa_db

Pop-Location

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "Usuários inseridos com sucesso!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Usuários inseridos com sucesso!" -ForegroundColor Yellow
    Write-Host "Verifique o arquivo users.config para ver quais usuários foram criados." -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "Erro ao inserir usuários!" -ForegroundColor Red
    exit 1
}

