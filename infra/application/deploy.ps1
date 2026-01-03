# Script de Deploy para Railway
# Execute primeiro: npx -y @railway/cli login
# Depois execute: .\deploy.ps1
#
# Para restaurar backup: .\deploy.ps1 -RestoreBackup -BackupPath "caminho\para\backup.sql"

param(
    [string]$ProjectId = "204b4976-3e8c-4369-bc84-c9a3e0349b36",
    [string]$EnvironmentId = "31abc2e2-ee31-40a1-abd6-9fcf5aeda9b9",
    [string]$BackendServiceName = "backend",
    [string]$FrontendServiceName = "frontend",
    [string]$PostgresServiceName = "Postgres",
    [switch]$RestoreBackup,
    [string]$BackupPath = ""
)

# Função wrapper para executar Railway CLI via npx
function Invoke-Railway {
    param([string[]]$Arguments)
    npx -y @railway/cli $Arguments
}

Write-Host "=== Deploy Werner Advogados no Railway ===" -ForegroundColor Cyan
Write-Host ""

# Verificar se está logado
Write-Host "Verificando autenticação..." -ForegroundColor Yellow
$whoami = Invoke-Railway @("whoami") 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO: Você precisa fazer login no Railway primeiro!" -ForegroundColor Red
    Write-Host "Execute: npx -y @railway/cli login" -ForegroundColor Yellow
    exit 1
}

Write-Host "Autenticado: $whoami" -ForegroundColor Green
Write-Host ""

# Linkar ao projeto
Write-Host "Linkando ao projeto..." -ForegroundColor Yellow
Invoke-Railway @("link", "--project", $ProjectId) 2>&1 | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Tentando linkar sem especificar projeto..." -ForegroundColor Yellow
    Invoke-Railway @("link") 2>&1 | Out-Null
}

Write-Host ""
Write-Host "=== Configurando serviços ===" -ForegroundColor Cyan
Write-Host "Usando nomes dos serviços:" -ForegroundColor Gray
Write-Host "  - Backend: $BackendServiceName" -ForegroundColor Gray
Write-Host "  - Frontend: $FrontendServiceName" -ForegroundColor Gray
Write-Host "  - PostgreSQL: $PostgresServiceName" -ForegroundColor Gray
Write-Host ""
Write-Host "=== Configurando variáveis de ambiente ===" -ForegroundColor Cyan
Write-Host ""

# Configurar Backend
Write-Host "Configurando Backend ($BackendServiceName)..." -ForegroundColor Yellow
Invoke-Railway @("service", $BackendServiceName) 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Write-Host "  - Configurando variáveis do banco de dados..." -ForegroundColor Gray
    Invoke-Railway @("variables", "set", "SPRING_DATASOURCE_URL=`${{$PostgresServiceName.DATABASE_URL}}") 2>&1 | Out-Null
    Invoke-Railway @("variables", "set", "SPRING_DATASOURCE_USERNAME=`${{$PostgresServiceName.PGUSER}}") 2>&1 | Out-Null
    Invoke-Railway @("variables", "set", "SPRING_DATASOURCE_PASSWORD=`${{$PostgresServiceName.PGPASSWORD}}") 2>&1 | Out-Null
    Invoke-Railway @("variables", "set", "SPRING_JPA_HIBERNATE_DDL_AUTO=validate") 2>&1 | Out-Null
    Invoke-Railway @("variables", "set", "SPRING_JPA_SHOW_SQL=false") 2>&1 | Out-Null
    Invoke-Railway @("variables", "set", "SERVER_PORT=`${{PORT}}") 2>&1 | Out-Null
    
    # Verificar se JWT_SECRET já existe
    $jwtExists = Invoke-Railway @("variables", "get", "JWT_SECRET") 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  - Gerando JWT_SECRET..." -ForegroundColor Gray
        $jwtSecret = [Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
        Invoke-Railway @("variables", "set", "JWT_SECRET=$jwtSecret") 2>&1 | Out-Null
        Write-Host "  - JWT_SECRET configurado!" -ForegroundColor Green
    } else {
        Write-Host "  - JWT_SECRET já existe" -ForegroundColor Gray
    }
    
    Invoke-Railway @("variables", "set", "JWT_EXPIRATION=86400000") 2>&1 | Out-Null
    Write-Host "  - Backend configurado!" -ForegroundColor Green
} else {
    Write-Host "  - Serviço backend não encontrado. Crie-o primeiro no dashboard." -ForegroundColor Yellow
}

Write-Host ""

# Configurar Frontend
Write-Host "Configurando Frontend ($FrontendServiceName)..." -ForegroundColor Yellow
Invoke-Railway @("service", $FrontendServiceName) 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Invoke-Railway @("variables", "set", "VITE_API_URL=/api") 2>&1 | Out-Null
    
    # Verificar se BACKEND_URL já existe
    $backendUrlExists = Invoke-Railway @("variables", "get", "BACKEND_URL") 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  - BACKEND_URL não configurado. Configure após obter domínio do backend." -ForegroundColor Yellow
        Write-Host "  - Execute: npx -y @railway/cli variables set BACKEND_URL=https://seu-backend.railway.app --service frontend" -ForegroundColor Gray
    } else {
        Write-Host "  - BACKEND_URL já configurado" -ForegroundColor Gray
    }
    
    Write-Host "  - Frontend configurado!" -ForegroundColor Green
} else {
    Write-Host "  - Serviço frontend não encontrado. Crie-o primeiro no dashboard." -ForegroundColor Yellow
}

Write-Host ""

# Configurar PostgreSQL
Write-Host "Configurando PostgreSQL ($PostgresServiceName)..." -ForegroundColor Yellow
Invoke-Railway @("service", $PostgresServiceName) 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Invoke-Railway @("variables", "set", "POSTGRES_DB=wa_db") 2>&1 | Out-Null
    Write-Host "  - PostgreSQL configurado!" -ForegroundColor Green
} else {
    Write-Host "  - Serviço postgres não encontrado. Crie-o primeiro no dashboard." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Fazendo deploy ===" -ForegroundColor Cyan
Write-Host ""

# Deploy Backend
Write-Host "Deploy do Backend ($BackendServiceName)..." -ForegroundColor Yellow
Invoke-Railway @("service", $BackendServiceName) 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Invoke-Railway @("up", "--detach") 2>&1
    Write-Host "  - Deploy do backend iniciado!" -ForegroundColor Green
} else {
    Write-Host "  - Não foi possível fazer deploy do backend" -ForegroundColor Red
}

Write-Host ""

# Deploy Frontend
Write-Host "Deploy do Frontend ($FrontendServiceName)..." -ForegroundColor Yellow
Invoke-Railway @("service", $FrontendServiceName) 2>&1 | Out-Null
if ($LASTEXITCODE -eq 0) {
    Invoke-Railway @("up", "--detach") 2>&1
    Write-Host "  - Deploy do frontend iniciado!" -ForegroundColor Green
} else {
    Write-Host "  - Não foi possível fazer deploy do frontend" -ForegroundColor Red
}

Write-Host ""

# Restaurar backup se solicitado
if ($RestoreBackup) {
    Write-Host "=== Restaurando Backup do PostgreSQL ===" -ForegroundColor Cyan
    Write-Host ""
    
    if ([string]::IsNullOrWhiteSpace($BackupPath)) {
        Write-Host "ERRO: Você precisa especificar o caminho do arquivo de backup!" -ForegroundColor Red
        Write-Host "Uso: .\deploy.ps1 -RestoreBackup -BackupPath `"caminho\para\backup.sql`"" -ForegroundColor Yellow
        exit 1
    }
    
    if (-not (Test-Path $BackupPath)) {
        Write-Host "ERRO: Arquivo de backup não encontrado: $BackupPath" -ForegroundColor Red
        exit 1
    }
    
    Write-Host "Usando script melhorado para restore com diagnóstico..." -ForegroundColor Yellow
    Write-Host ""
    
    # Verificar se o script existe
    $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
    $restoreScript = Join-Path $scriptDir "infra\database\restore_backup_railway.ps1"
    
    if (Test-Path $restoreScript) {
        Write-Host "Executando script de restore melhorado..." -ForegroundColor Cyan
        Write-Host ""
        
        & $restoreScript -BackupPath $BackupPath -PostgresServiceName $PostgresServiceName -ProjectId $ProjectId
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "✅ Restore concluído com sucesso!" -ForegroundColor Green
        } else {
            Write-Host ""
            Write-Host "❌ Erro ao executar restore. Verifique as mensagens acima." -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "⚠️  Script melhorado não encontrado. Usando método básico..." -ForegroundColor Yellow
        Write-Host ""
        
        Write-Host "Restaurando backup de: $BackupPath" -ForegroundColor Yellow
        Write-Host "⚠️  ATENÇÃO: Isso irá SOBRESCREVER todos os dados existentes no banco!" -ForegroundColor Red
        Write-Host ""
        
        # Verificar status do PostgreSQL primeiro
        Write-Host "Verificando status do PostgreSQL..." -ForegroundColor Yellow
        $logs = npx -y @railway/cli logs --service $PostgresServiceName --tail 20 2>&1
        
        $isLooping = $false
        foreach ($line in $logs) {
            if ($line -like "*restarting*" -or $line -like "*exited with code*" -or $line -like "*FATAL*") {
                $isLooping = $true
                break
            }
        }
        
        if ($isLooping) {
            Write-Host ""
            Write-Host "⚠️  PROBLEMA DETECTADO: PostgreSQL parece estar em loop de restart!" -ForegroundColor Red
            Write-Host ""
            Write-Host "Para resolver:" -ForegroundColor Yellow
            Write-Host "  1. Use o script melhorado: .\infra\database\restore_backup_railway_fix.ps1 -BackupPath `"$BackupPath`"" -ForegroundColor Gray
            Write-Host "  2. Ou pare e recrie o serviço PostgreSQL no Railway Dashboard" -ForegroundColor Gray
            Write-Host ""
            exit 1
        }
        
        # Obter DATABASE_PUBLIC_URL
        Write-Host "Obtendo URL do banco de dados..." -ForegroundColor Yellow
        $varsOutput = npx -y @railway/cli variables --service $PostgresServiceName 2>&1
        $dbPublicUrl = $null
        
        foreach ($line in $varsOutput) {
            if ($line -like '*DATABASE_PUBLIC_URL*' -and $line -like '*postgresql://*') {
                $urlStart = $line.IndexOf('postgresql://')
                if ($urlStart -ge 0) {
                    $urlPart = $line.Substring($urlStart)
                    $dbPublicUrl = ($urlPart -split '\s')[0]
                    break
                }
            }
        }
        
        if ([string]::IsNullOrWhiteSpace($dbPublicUrl)) {
            Write-Host "ERRO: Não foi possível obter DATABASE_PUBLIC_URL" -ForegroundColor Red
            Write-Host "Execute: npx -y @railway/cli variables --service $PostgresServiceName" -ForegroundColor Yellow
            exit 1
        }
        
        Write-Host "URL obtida!" -ForegroundColor Green
        Write-Host ""
        
        # Verificar psql
        $psqlPath = "C:\Program Files\PostgreSQL\16\bin\psql.exe"
        if (-not (Test-Path $psqlPath)) {
            $psqlFound = where.exe psql 2>$null
            if ($psqlFound) {
                $psqlPath = $psqlFound
            } else {
                Write-Host "ERRO: psql não encontrado. Instale o PostgreSQL client." -ForegroundColor Red
                Write-Host "Ou execute manualmente: psql `"$dbPublicUrl`" -f `"$BackupPath`"" -ForegroundColor Yellow
                exit 1
            }
        }
        
        # Executar restore
        Write-Host "Executando restore..." -ForegroundColor Cyan
        Write-Host ""
        
        try {
            & $psqlPath $dbPublicUrl -f $BackupPath
            
            if ($LASTEXITCODE -eq 0) {
                Write-Host ""
                Write-Host "✅ Backup restaurado com sucesso!" -ForegroundColor Green
            } else {
                Write-Host ""
                Write-Host "❌ Erro ao restaurar backup. Código: $LASTEXITCODE" -ForegroundColor Red
                exit 1
            }
        } catch {
            Write-Host ""
            Write-Host "❌ Erro ao executar restore: $_" -ForegroundColor Red
            Write-Host "Execute manualmente: psql `"$dbPublicUrl`" -f `"$BackupPath`"" -ForegroundColor Yellow
            exit 1
        }
    }
    
    Write-Host ""
    exit 0
}

Write-Host "=== Deploy concluído! ===" -ForegroundColor Green
Write-Host ""
Write-Host "Próximos passos:" -ForegroundColor Cyan
Write-Host "1. Aguarde o deploy completar (verifique: npx -y @railway/cli logs --service $BackendServiceName)"
Write-Host "2. Configure domínios públicos:"
Write-Host "   - Backend: npx -y @railway/cli domain --service $BackendServiceName"
Write-Host "   - Frontend: npx -y @railway/cli domain --service $FrontendServiceName"
Write-Host "3. Após obter os domínios:"
Write-Host "   - Configure BACKEND_URL no frontend: npx -y @railway/cli variables set BACKEND_URL=https://seu-backend.railway.app --service $FrontendServiceName"
Write-Host "   - Configure CORS no backend: npx -y @railway/cli variables set CORS_ALLOWED_ORIGINS=https://seu-frontend.railway.app --service $BackendServiceName"
Write-Host "4. Inicialize o banco OU restaure backup:"
Write-Host "   - Para schema inicial: npx -y @railway/cli run --service $PostgresServiceName psql `$DATABASE_URL < infra/database/generate_database.sql"
Write-Host "   - Para restaurar backup: .\deploy.ps1 -RestoreBackup -BackupPath `"caminho\para\backup.sql`""
Write-Host ""
Write-Host "Para ver os logs: npx -y @railway/cli logs --service [nome-do-servico]" -ForegroundColor Yellow
Write-Host "Nomes dos serviços configurados:" -ForegroundColor Cyan
Write-Host "  - Backend: $BackendServiceName" -ForegroundColor Gray
Write-Host "  - Frontend: $FrontendServiceName" -ForegroundColor Gray
Write-Host "  - PostgreSQL: $PostgresServiceName" -ForegroundColor Gray
Write-Host "Para abrir o dashboard: npx -y @railway/cli open" -ForegroundColor Yellow

