# Guia Rápido: Configurando Variáveis no Railway

Este guia explica como configurar variáveis de ambiente sensíveis no Railway **sem** commitá-las no git.

> **💡 Config as Code:** O Railway suporta Config as Code através de arquivos `railway.json` ou `railway.toml`. Os arquivos `backend/railway.json` e `frontend/railway.json` já foram criados para configurar automaticamente o uso do Dockerfile. Veja mais em: [Railway Config as Code](https://docs.railway.com/guides/config-as-code)

> **💡 Config as Code:** O Railway suporta Config as Code através de arquivos `railway.json` ou `railway.toml`. Os arquivos `backend/railway.json` e `frontend/railway.json` já foram criados para configurar automaticamente o uso do Dockerfile. Veja mais em: [Railway Config as Code](https://docs.railway.com/guides/config-as-code)

## 🚀 Passo 0: Criar o Projeto e Serviços no Railway

**IMPORTANTE:** Antes de configurar variáveis, você precisa criar os serviços no Railway.

**📌 Entendendo a estrutura:**
- Você terá **1 projeto** no Railway
- Dentro desse projeto, você criará **3 serviços separados**:
  1. **PostgreSQL** (banco de dados)
  2. **Backend** (aplicação Spring Boot)
  3. **Frontend** (aplicação Vue.js)
- Cada serviço é **independente** e precisa ser criado separadamente
- Cada serviço tem seu próprio **Root Directory** (pasta do repositório)

### Criar o Projeto:

1. Acesse [railway.app](https://railway.app) e faça login
2. Clique em **New Project**
3. Selecione **Deploy from GitHub repo** (ou **Empty Project** se preferir)

### Criar o Serviço PostgreSQL:

1. No seu projeto, clique em **+ New** (botão no canto superior direito) ou **Add Service**
2. No menu que aparece, procure por **Database** ou **PostgreSQL**
3. Clique em **Add PostgreSQL** ou **Provision PostgreSQL**
4. O Railway criará automaticamente um banco PostgreSQL gerenciado
5. **Aguarde alguns segundos** até o serviço estar pronto (status verde)
6. Você verá o serviço aparecer na lista com o nome "Postgres" ou "PostgreSQL" e um ícone de banco de dados 🗄️

**Dica:** Se não encontrar a opção "Database", procure por "Provision" ou "Add Resource". O PostgreSQL pode estar em diferentes lugares dependendo da versão do Railway.

### Criar o Serviço Backend:

1. No mesmo projeto, clique em **+ New** ou **Add Service**
2. Selecione **GitHub Repo** → escolha seu repositório
3. **Configure o Root Directory como `backend`** ⚠️ **IMPORTANTE:**
   - Cada serviço precisa ter seu próprio Root Directory
   - O Root Directory define qual pasta do repositório será usada para o build
   - Se você colocar `backend` aqui, o Railway só verá os arquivos da pasta `backend/`
   - **NÃO** conseguirá fazer deploy do frontend a partir deste serviço
4. **Configure a Branch:**
   - Durante a criação, você verá uma opção **Branch** ou **Git Branch**
   - Selecione a branch que deseja usar (ex: `main`, `master`, `develop`)
   - Por padrão, o Railway usa a branch `main` ou `master`
5. O Railway detectará o `Dockerfile` automaticamente (deve estar em `backend/Dockerfile`)
6. **IMPORTANTE:** Se o Railway usar Railpack em vez do Dockerfile:
   - Vá em **Settings** → **Build & Deploy**
   - Em **Build Command**, deixe vazio ou remova qualquer comando
   - Em **Dockerfile Path**, certifique-se de que está como `Dockerfile`
   - Ou selecione **Docker** como builder em vez de **Railpack**

**💡 Para alterar a branch depois de criar o serviço:**
- Vá em **Settings** → **Source**
- Em **Branch**, selecione a branch desejada
- O Railway fará um novo deploy automaticamente

### Criar o Serviço Frontend:

1. No mesmo projeto, clique em **+ New** ou **Add Service** novamente
2. Selecione **GitHub Repo** → escolha seu repositório (mesmo repositório)
3. **Configure o Root Directory como `frontend`** ⚠️ **IMPORTANTE:**
   - Este é um **serviço SEPARADO** do backend
   - Você precisa criar um **novo serviço** para o frontend
   - Configure o Root Directory como `frontend` (não `backend`)
   - Cada serviço (backend e frontend) é independente e precisa ser criado separadamente
4. **Configure a Branch:**
   - Durante a criação, você verá uma opção **Branch** ou **Git Branch**
   - Selecione a mesma branch usada no backend (ex: `main`, `master`, `develop`)
   - Por padrão, o Railway usa a branch `main` ou `master`
5. O Railway detectará o `Dockerfile` automaticamente (deve estar em `frontend/Dockerfile`)
6. **Config as Code:** O arquivo `frontend/railway.json` já está configurado para usar o Dockerfile automaticamente
7. **IMPORTANTE:** Se o Railway usar Railpack em vez do Dockerfile:
   - O arquivo `railway.json` deve forçar o uso do Dockerfile
   - Se ainda não funcionar, vá em **Settings** → **Build & Deploy**
   - Em **Build Command**, deixe vazio ou remova qualquer comando
   - Em **Dockerfile Path**, certifique-se de que está como `Dockerfile`
   - Ou selecione **Docker** como builder em vez de **Railpack**

**💡 Para alterar a branch depois de criar o serviço:**
- Vá em **Settings** → **Source**
- Em **Branch**, selecione a branch desejada
- O Railway fará um novo deploy automaticamente

### ⚠️ Entendendo Root Directory:

**Cada serviço no Railway é independente:**
- **Backend** = Um serviço com Root Directory `backend/`
- **Frontend** = Outro serviço (separado) com Root Directory `frontend/`
- **PostgreSQL** = Outro serviço (banco de dados gerenciado)

**Você NÃO pode:**
- ❌ Fazer deploy do frontend usando Root Directory `backend/`
- ❌ Fazer deploy do backend usando Root Directory `frontend/`
- ❌ Ter ambos em um único serviço

**Você DEVE:**
- ✅ Criar **3 serviços separados**: PostgreSQL, Backend e Frontend
- ✅ Cada um com seu próprio Root Directory correto
- ✅ Todos no mesmo projeto Railway (mas serviços diferentes)

---

## 📋 Config as Code (Configuração via Arquivos)

O Railway suporta **Config as Code** através de arquivos `railway.json` ou `railway.toml`. Isso permite versionar as configurações junto com o código.

### ✅ Arquivos já criados:

- `backend/railway.json` - Configura o backend para usar Dockerfile
- `frontend/railway.json` - Configura o frontend para usar Dockerfile

### O que esses arquivos fazem:

- **Forçam o uso do Dockerfile** em vez do Railpack
- **Definem comandos de start** para cada serviço
- **Configuram políticas de restart**

### ⚠️ Variáveis de Ambiente:

**IMPORTANTE:** As variáveis de ambiente **NÃO** podem ser definidas nos arquivos `railway.json` por questões de segurança. Elas devem ser configuradas no Dashboard do Railway (veja seção abaixo).

### 📝 Personalizando Config as Code:

Você pode editar os arquivos `railway.json` para adicionar mais configurações:
- `buildCommand`: Comando customizado de build
- `preDeployCommand`: Comando executado antes do deploy
- `healthcheckPath`: Caminho para healthcheck
- `restartPolicyType`: Política de restart

**Documentação completa:** [Railway Config as Code](https://docs.railway.com/guides/config-as-code)

---

## 📋 Opção 1: Configurar Variáveis via Dashboard do Railway (Recomendado)

### 📍 Onde encontrar a aba Variables:

1. Acesse [railway.app](https://railway.app) e faça login
2. Selecione seu **projeto**
3. Na lista de serviços, **clique no serviço** que deseja configurar (PostgreSQL, Backend ou Frontend)
4. No menu superior do serviço, você verá várias abas: **Deployments**, **Metrics**, **Variables**, **Settings**, etc.
5. **Clique na aba "Variables"** (ou "Environment Variables")
6. Você verá uma lista de variáveis existentes e um botão **+ New Variable** ou **+ Add Variable**

### Para o Serviço PostgreSQL:

1. No seu projeto no [Railway Dashboard](https://railway.app)
2. Clique no serviço **PostgreSQL** (deve aparecer na lista de serviços)
3. Clique na aba **Variables** (no topo da página do serviço)
4. Adicione a variável customizada:
   - Clique em **+ New Variable** ou **+ Add Variable**
   - **Nome**: `POSTGRES_DB`
   - **Valor**: `wa_db`
   - Clique em **Add** ou **Save**

**Nota:** As outras variáveis (`POSTGRES_USER`, `POSTGRES_PASSWORD`, `DATABASE_URL`) são geradas automaticamente pelo Railway e aparecem na mesma aba **Variables**. Você pode copiar essas variáveis para usar no backend.

### Para o Serviço Backend:

1. No seu projeto, clique no serviço **Backend**
2. Clique na aba **Variables** (no topo da página)
3. Adicione cada variável uma por uma:
   - Clique em **+ New Variable** ou **+ Add Variable**
   - Preencha o **Nome** e o **Valor**
   - Clique em **Add** ou **Save**
   - Repita para cada variável abaixo

**Variáveis para adicionar:**

| Nome | Valor | Observações |
|------|-------|-------------|
| `SPRING_DATASOURCE_URL` | `${{Postgres.DATABASE_URL}}` | Use a referência do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `${{Postgres.PGUSER}}` | Use a referência do PostgreSQL |
| `SPRING_DATASOURCE_PASSWORD` | `${{Postgres.PGPASSWORD}}` | Use a referência do PostgreSQL |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `validate` | Texto fixo |
| `SPRING_JPA_SHOW_SQL` | `false` | Texto fixo |
| `SERVER_PORT` | `${{PORT}}` | Use a referência do Railway |
| `CORS_ALLOWED_ORIGINS` | `${{Frontend.RAILWAY_PUBLIC_DOMAIN}}` | Configure após frontend estar deployado |
| `JWT_SECRET` | `sua-chave-secreta-forte-aqui` | ⚠️ **SUBSTITUA** por uma chave forte |
| `JWT_EXPIRATION` | `86400000` | Texto fixo (opcional) |

**⚠️ IMPORTANTE:**
- **Para usar referências do PostgreSQL:** Na aba Variables do serviço PostgreSQL, você verá variáveis como `DATABASE_URL`, `PGUSER`, `PGPASSWORD`. Use `${{Postgres.NOME_DA_VARIAVEL}}` no backend.
- **Para JWT_SECRET:** Substitua `sua-chave-secreta-forte-aqui` por uma chave secreta forte (mínimo 32 caracteres)
  - Gerar chave no Windows: `[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))`
  - Gerar chave no Linux/Mac: `openssl rand -base64 32`
- **CORS_ALLOWED_ORIGINS:** Só pode ser configurada após o frontend estar deployado e ter um domínio público

### Para o Serviço Frontend:

1. No seu projeto, clique no serviço **Frontend**
2. Clique na aba **Variables** (no topo da página)
3. Adicione a variável:
   - Clique em **+ New Variable** ou **+ Add Variable**
   - **Nome**: `VITE_API_URL`
   - **Valor**: `/api`
   - Clique em **Add** ou **Save**

**Variável para adicionar:**

| Nome | Valor |
|------|-------|
| `VITE_API_URL` | `/api` |

**Nota:** Esta é a única variável necessária para o frontend. O Railway gerará automaticamente outras variáveis como `PORT` e `RAILWAY_PUBLIC_DOMAIN`.

## 📋 Opção 2: Usar Railway CLI (Avançado)

Se você tem o Railway CLI instalado:

```bash
# Login
railway login

# Link ao projeto
railway link

# Configurar variáveis (substitua SERVICE_NAME pelo nome do serviço)
railway variables set JWT_SECRET=sua-chave-secreta-forte-aqui --service SERVICE_NAME

# Ver todas as variáveis
railway variables
```

## 🔐 Gerando uma Chave JWT Segura

### No Linux/Mac:

```bash
openssl rand -base64 32
```

### No Windows (PowerShell):

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

### Online:

Use um gerador seguro como: https://www.random.org/strings/

## ✅ Checklist de Configuração

### Antes de Começar:
- [ ] Projeto criado no Railway
- [ ] Serviço PostgreSQL criado e rodando (status verde)
- [ ] Serviço Backend criado (pode estar com erro até configurar variáveis)
- [ ] Serviço Frontend criado (pode estar com erro até configurar variáveis)

### Configuração:
- [ ] PostgreSQL: `POSTGRES_DB=wa_db` configurado
- [ ] Backend: Todas as variáveis configuradas (veja seção acima)
- [ ] Backend: `JWT_SECRET` configurado com chave forte e única
- [ ] Frontend: `VITE_API_URL=/api` configurado
- [ ] SQL de inicialização executado (`infra/database/generate_database.sql`)

### Após Deploy:
- [ ] Backend: `CORS_ALLOWED_ORIGINS` configurado com URL do frontend
- [ ] Frontend: Domínio público gerado
- [ ] Teste de acesso funcionando

## 🔒 Segurança

**NUNCA faça:**
- ❌ Commit arquivos `.env` no git
- ❌ Compartilhar chaves secretas em mensagens ou emails
- ❌ Usar a mesma chave JWT em diferentes ambientes

**SEMPRE faça:**
- ✅ Configure variáveis diretamente no Railway Dashboard
- ✅ Use chaves diferentes para desenvolvimento e produção
- ✅ Mantenha as chaves em local seguro (gerenciador de senhas)

## 📚 Arquivos de Referência

- `.env.example` - Exemplo para desenvolvimento local
- `.env.railway.example` - Exemplo para Railway (referência)
- `RAILWAY.md` - Documentação completa do deploy

## 🆘 Problemas Comuns

### Não consigo encontrar o serviço PostgreSQL:
1. **Certifique-se de que criou o serviço primeiro:**
   - Vá em **+ New** → **Database** → **Add PostgreSQL**
   - Aguarde alguns segundos até aparecer na lista de serviços
2. Se ainda não aparecer, verifique se está no projeto correto
3. O serviço PostgreSQL aparece com um ícone de banco de dados 🗄️

### Não sei onde encontrar a aba Variables:
1. **Certifique-se de que está dentro de um serviço específico** (não no projeto geral)
2. **No topo da página do serviço**, procure por abas como: Deployments, Metrics, **Variables**, Settings, Logs
3. **A aba Variables pode ter nomes diferentes:**
   - "Variables"
   - "Environment Variables"
   - "Env Vars"
   - "Environment"
4. **Se não encontrar:** Verifique se você clicou no serviço correto (PostgreSQL, Backend ou Frontend)

### Variável não está funcionando:
1. Verifique se o nome da variável está correto (case-sensitive)
2. Certifique-se de que salvou a variável no Railway (clique em **Add** ou **Save**)
3. Reinicie o serviço após adicionar variáveis:
   - Vá em **Settings** → **Restart** ou use o botão de restart no topo da página
4. Verifique se está usando a sintaxe correta para referências:
   - `${{Postgres.DATABASE_URL}}` (com chaves duplas)
   - `${{PORT}}` (com chaves duplas)

### Como configurar/alterar a branch do GitHub:
1. **No serviço (Backend ou Frontend), vá em Settings → Source**
2. **Em "Branch", selecione a branch desejada** (ex: `main`, `master`, `develop`)
3. **Salve as alterações** - o Railway fará um novo deploy automaticamente
4. **Nota:** Certifique-se de que a branch selecionada existe no seu repositório GitHub

### Railway está usando Railpack em vez do Dockerfile:
1. **No serviço (Backend ou Frontend), vá em Settings → Build & Deploy**
2. **Altere o Builder de "Railpack" para "Docker"**
3. **Ou configure manualmente:**
   - **Dockerfile Path**: `Dockerfile`
   - **Build Command**: (deixe vazio)
   - **Start Command**: (deixe vazio, o Dockerfile já define)
4. **Salve as alterações** e faça um novo deploy
5. **Alternativa:** Os arquivos `railway.json` foram criados nas pastas `backend/` e `frontend/` para forçar o uso do Dockerfile

### CORS não funciona:
1. Certifique-se de que `CORS_ALLOWED_ORIGINS` contém a URL exata do frontend
2. Inclua `https://` na URL
3. Reinicie o backend após alterar CORS

### JWT não funciona:
1. Verifique se `JWT_SECRET` está configurado
2. Certifique-se de que a chave tem pelo menos 32 caracteres
3. Verifique os logs do backend para erros

