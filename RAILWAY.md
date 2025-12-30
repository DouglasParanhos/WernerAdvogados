# Guia de Deploy no Railway

Este guia explica como fazer o deploy completo do sistema Werner Advogados na plataforma Railway.

## Pré-requisitos

- Conta no [Railway.app](https://railway.app)
- Projeto no GitHub (recomendado) ou acesso via Railway CLI
- Railway CLI instalado (opcional, mas útil): `npm i -g @railway/cli`

## Opções de Deploy

O Railway oferece duas formas principais de fazer deploy:

### Opção 1: Deploy com Docker Compose (Mais Simples)

O Railway suporta `docker-compose.yml`, mas precisamos fazer alguns ajustes.

#### Passo 1: Criar arquivo `docker-compose.railway.yml`

Crie um arquivo `docker-compose.railway.yml` na raiz do projeto com o seguinte conteúdo:

```yaml
services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: ${POSTGRES_DB:-wa_db}
      POSTGRES_USER: ${POSTGRES_USER:-postgres}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./werneradv20250511.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-postgres}" ]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB:-wa_db}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER:-postgres}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
      SPRING_JPA_SHOW_SQL: "false"
      SERVER_PORT: ${PORT:-8081}
      CORS_ALLOWED_ORIGINS: ${CORS_ALLOWED_ORIGINS}
    depends_on:
      postgres:
        condition: service_healthy
    expose:
      - "8081"

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
      args:
        VITE_API_URL: /api
    environment:
      - PORT=80
    ports:
      - "${PORT:-80}:80"
    depends_on:
      - backend

volumes:
  postgres_data:
```

#### Passo 2: Ajustar `nginx.conf` para Railway

O Railway atribui uma porta dinamicamente via variável `PORT`. Atualize o arquivo `frontend/nginx.conf`:

```nginx
server {
    listen ${PORT:-80};
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8081/api;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

#### Passo 3: Deploy no Railway

1. Acesse [railway.app](https://railway.app) e faça login
2. Clique em **New Project**
3. Selecione **Deploy from GitHub repo** (ou faça upload do código)
4. Selecione seu repositório
5. Railway detectará o `docker-compose.yml` automaticamente
6. Configure as variáveis de ambiente no dashboard:
   - `POSTGRES_PASSWORD`: Senha forte para o banco
   - `CORS_ALLOWED_ORIGINS`: URL do frontend (será configurada após o deploy)

---

### Opção 2: Deploy Separado (Recomendado)

Esta é a forma mais recomendada e confiável no Railway, onde cada serviço é deployado separadamente.

#### Passo 1: Criar Projeto PostgreSQL no Railway

1. Acesse [railway.app](https://railway.app)
2. Faça login ou crie uma conta
3. Clique em **New Project**
4. Selecione **Add PostgreSQL**
5. O Railway criará automaticamente um banco PostgreSQL gerenciado
6. Anote as variáveis de conexão (Railway fornece automaticamente)

#### Passo 2: Configurar Banco de Dados

1. No serviço PostgreSQL, vá na aba **Variables**
2. Adicione uma variável customizada:
   - Nome: `POSTGRES_DB`
   - Valor: `wa_db`

3. **Inicializar o banco de dados:**

   **Opção A: Via Railway CLI (Recomendado)**
   
   ```bash
   # Instalar Railway CLI
   npm i -g @railway/cli
   
   # Login
   railway login
   
   # Link ao projeto
   railway link
   
   # Executar SQL (substitua $DATABASE_URL pela URL do Railway)
   railway run psql $DATABASE_URL < werneradv20250511.sql
   ```
   
   **Opção B: Via Dashboard**
   
   1. No serviço PostgreSQL, vá na aba **Data**
   2. Use o **Query Editor** para executar o conteúdo do arquivo `werneradv20250511.sql`
   3. Ou faça upload do arquivo SQL

#### Passo 3: Deploy do Backend

1. No mesmo projeto, clique em **New Service**
2. Selecione **Deploy from GitHub repo** (ou via CLI)
3. Selecione a pasta `backend` do repositório
4. Railway detectará o `Dockerfile` automaticamente
5. Configure as **Variáveis de Ambiente**:

   ```
   SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
   SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
   SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
   SPRING_JPA_HIBERNATE_DDL_AUTO=validate
   SPRING_JPA_SHOW_SQL=false
   SERVER_PORT=${{PORT}}
   CORS_ALLOWED_ORIGINS=${{Frontend.RAILWAY_PUBLIC_DOMAIN}}
   ```

   **Nota:** `${{Postgres.DATABASE_URL}}` é uma referência automática do Railway que conecta os serviços. Você pode encontrá-la na aba **Variables** do serviço PostgreSQL.

6. Aguarde o build e deploy completarem

#### Passo 4: Deploy do Frontend

1. No mesmo projeto, clique em **New Service**
2. Selecione **Deploy from GitHub repo**
3. Selecione a pasta `frontend` do repositório
4. Railway detectará o `Dockerfile` automaticamente
5. Configure as **Variáveis de Ambiente**:

   ```
   VITE_API_URL=/api
   ```

6. **Ajustar Nginx para Railway:**

   O Railway usa uma variável `PORT` dinâmica. Você precisa atualizar o `frontend/Dockerfile`:

   ```dockerfile
   FROM node:18-alpine AS build
   WORKDIR /app
   COPY package*.json ./
   RUN npm install
   COPY . .
   ARG VITE_API_URL=/api
   ENV VITE_API_URL=$VITE_API_URL
   RUN npm run build

   FROM nginx:alpine
   COPY --from=build /app/dist /usr/share/nginx/html
   COPY nginx.conf /etc/nginx/conf.d/default.conf

   # Railway usa variável PORT
   RUN apk add --no-cache envsubst
   RUN echo '#!/bin/sh' > /entrypoint.sh && \
       echo 'envsubst "\$PORT" < /etc/nginx/conf.d/default.conf > /tmp/default.conf' && \
       echo 'mv /tmp/default.conf /etc/nginx/conf.d/default.conf' && \
       echo 'exec nginx -g "daemon off;"' >> /entrypoint.sh && \
       chmod +x /entrypoint.sh

   EXPOSE $PORT
   CMD ["/entrypoint.sh"]
   ```

   E atualizar `frontend/nginx.conf`:

   ```nginx
   server {
       listen ${PORT} default_server;
       server_name _;
       root /usr/share/nginx/html;
       index index.html;

       location / {
           try_files $uri $uri/ /index.html;
       }

       location /api {
           proxy_pass http://backend:8081/api;
           proxy_http_version 1.1;
           proxy_set_header Upgrade $http_upgrade;
           proxy_set_header Connection 'upgrade';
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
           proxy_cache_bypass $http_upgrade;
       }
   }
   ```

   **Nota:** Se preferir uma solução mais simples, você pode usar o `nginx.conf` original e o Railway ajustará automaticamente a porta.

#### Passo 5: Conectar Serviços

1. No serviço **Frontend**, vá em **Settings** → **Networking**
2. Adicione o **Backend** como dependência (isso garante que o frontend só inicie após o backend)
3. No serviço **Backend**, adicione o **PostgreSQL** como dependência

#### Passo 6: Configurar Domínio

1. No serviço **Frontend**, vá em **Settings** → **Domains**
2. Clique em **Generate Domain** (Railway gerará um domínio `.railway.app` gratuito)
3. Ou adicione um **Custom Domain** (domínio próprio)

#### Passo 7: Configurar CORS

Após o frontend estar deployado e ter um domínio:

1. No serviço **Backend**, vá em **Variables**
2. Atualize a variável `CORS_ALLOWED_ORIGINS`:
   ```
   CORS_ALLOWED_ORIGINS=https://seu-frontend.railway.app,https://www.seudominio.com
   ```
3. O Railway reiniciará o backend automaticamente

## Checklist de Deploy

- [ ] Conta Railway criada
- [ ] Projeto PostgreSQL criado e configurado
- [ ] Variável `POSTGRES_DB=wa_db` configurada
- [ ] SQL de inicialização (`werneradv20250511.sql`) executado
- [ ] Backend deployado com variáveis corretas
- [ ] Frontend deployado
- [ ] Serviços conectados (dependências configuradas)
- [ ] CORS configurado com URL do frontend
- [ ] Domínio configurado (Railway ou customizado)
- [ ] Teste de acesso funcionando

## Variáveis de Ambiente Importantes

### PostgreSQL
- `POSTGRES_DB`: Nome do banco (padrão: `wa_db`)
- `POSTGRES_USER`: Usuário (gerado automaticamente pelo Railway)
- `POSTGRES_PASSWORD`: Senha (gerada automaticamente pelo Railway)
- `DATABASE_URL`: URL completa de conexão (gerada automaticamente)

### Backend
- `SPRING_DATASOURCE_URL`: Use `${{Postgres.DATABASE_URL}}`
- `SPRING_DATASOURCE_USERNAME`: Use `${{Postgres.PGUSER}}`
- `SPRING_DATASOURCE_PASSWORD`: Use `${{Postgres.PGPASSWORD}}`
- `SERVER_PORT`: Use `${{PORT}}`
- `CORS_ALLOWED_ORIGINS`: URL do frontend (ex: `https://seu-app.railway.app`)

### Frontend
- `VITE_API_URL`: `/api` (para proxy do Nginx)

## Custos Railway

- **Plano Hobby**: $5/mês (inclui 500 horas grátis)
- **Plano Pro**: $20/mês (uso ilimitado)
- **PostgreSQL**: Incluído no plano
- **Domínio `.railway.app`**: Gratuito
- **Domínio customizado**: Gratuito (você precisa ter o domínio)

**Para 5-10 usuários simultâneos, o plano Hobby é mais que suficiente.**

## Solução de Problemas

### Backend não conecta ao banco

1. Verifique se as variáveis de ambiente estão corretas
2. Certifique-se de que o PostgreSQL está rodando
3. Verifique os logs: `railway logs backend` ou via dashboard

### Frontend não carrega

1. Verifique se o backend está respondendo
2. Verifique os logs do frontend
3. Certifique-se de que o CORS está configurado corretamente
4. Verifique se o proxy `/api` está funcionando

### Erro de CORS

1. Atualize `CORS_ALLOWED_ORIGINS` no backend com a URL exata do frontend
2. Certifique-se de incluir `https://` na URL
3. Reinicie o backend após alterar as variáveis

### Banco de dados não inicializa

1. Verifique se o arquivo SQL foi executado corretamente
2. Verifique os logs do PostgreSQL
3. Tente executar o SQL novamente via Query Editor

### Porta não encontrada

1. Certifique-se de que o `nginx.conf` usa `${PORT}` ou a porta padrão
2. Railway atribui portas automaticamente, não é necessário configurar manualmente

## Comandos Úteis Railway CLI

```bash
# Login
railway login

# Link ao projeto
railway link

# Ver logs
railway logs

# Ver logs de um serviço específico
railway logs backend

# Executar comando no serviço
railway run <comando>

# Abrir dashboard
railway open

# Ver variáveis de ambiente
railway variables
```

## Próximos Passos

Após o deploy bem-sucedido:

1. **Configurar backup automático** do banco de dados
2. **Configurar monitoramento** (Railway oferece métricas básicas)
3. **Configurar domínio customizado** (se necessário)
4. **Documentar credenciais** em local seguro
5. **Testar todas as funcionalidades** em produção

## Suporte

- [Documentação Railway](https://docs.railway.app)
- [Railway Discord](https://discord.gg/railway)
- [Status Railway](https://status.railway.app)

