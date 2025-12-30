# Script de Inserção de Usuários

Este diretório contém scripts para inserir usuários no banco de dados com senhas criptografadas usando BCrypt.

## Segurança

⚠️ **IMPORTANTE**: As senhas não estão nos arquivos versionados!

- O arquivo `users.config` está no `.gitignore` e contém as senhas reais
- Use `users.config.example` como template
- Nunca commite o arquivo `users.config` ou `insert_users.sql` no git

## Configuração Inicial

1. Copie o arquivo de exemplo:
   ```bash
   cp users.config.example users.config
   ```

2. Edite `users.config` e adicione as senhas reais:
   ```
   liz.werner:SUA_SENHA_AQUI:USER
   angelo.masullo:SUA_SENHA_AQUI:USER
   ```

## Arquivos

- `generate_users.py` - Script Python que gera os hashes BCrypt e cria o arquivo SQL
- `users.config.example` - Template de configuração (sem senhas)
- `users.config` - Arquivo de configuração real (não versionado, no .gitignore)
- `insert_users.sql` - Arquivo SQL gerado (não versionado, no .gitignore)
- `insert_users.ps1` - Script PowerShell para Windows
- `insert_users.sh` - Script Bash para Linux/Mac

## Como Usar

### Windows (PowerShell)

Execute a partir da raiz do projeto:
```powershell
.\infra\generate_users\insert_users.ps1
```

Ou entre na pasta:
```powershell
cd infra\generate_users
.\insert_users.ps1
```

### Linux/Mac (Bash)

Execute a partir da raiz do projeto:
```bash
chmod +x infra/generate_users/insert_users.sh
./infra/generate_users/insert_users.sh
```

Ou entre na pasta:
```bash
cd infra/generate_users
chmod +x insert_users.sh
./insert_users.sh
```

### Execução Manual

1. Configure o arquivo `users.config` (veja seção "Configuração Inicial" acima)

2. Entre na pasta:
   ```bash
   cd infra/generate_users
   ```

3. Gere o arquivo SQL:
   ```bash
   python generate_users.py > insert_users.sql
   ```

4. Execute no banco de dados:
   ```bash
   # Windows PowerShell
   Get-Content insert_users.sql | docker exec -i wa-postgres psql -U postgres -d wa_db
   
   # Linux/Mac
   docker exec -i wa-postgres psql -U postgres -d wa_db < insert_users.sql
   ```

### Usando Variáveis de Ambiente (Alternativa)

Você também pode definir os usuários via variáveis de ambiente ao invés de usar o arquivo `users.config`:

```bash
# Windows PowerShell
$env:USER1_USERNAME="username1"
$env:USER1_PASSWORD="senha_segura_123"
$env:USER1_ROLE="USER"
$env:USER2_USERNAME="username2"
$env:USER2_PASSWORD="outra_senha_segura"
$env:USER2_ROLE="USER"

# Linux/Mac
export USER1_USERNAME="username1"
export USER1_PASSWORD="senha_segura_123"
export USER1_ROLE="USER"
export USER2_USERNAME="username2"
export USER2_PASSWORD="outra_senha_segura"
export USER2_ROLE="USER"
```

## Requisitos

- Python 3.x
- Biblioteca `bcrypt` (instalar com `pip install bcrypt`)
- Docker com o container `wa-postgres` rodando

## Notas

- O script usa `ON CONFLICT` para atualizar usuários existentes
- As senhas são criptografadas usando BCrypt com 12 rounds
- O formato `$2a$` é usado para compatibilidade com Spring Security

