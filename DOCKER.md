# Guia de Execução com Docker

Este guia explica como executar a aplicação usando Docker e Docker Compose.

## Pré-requisitos

- Docker 20.10 ou superior
- Docker Compose 2.0 ou superior

## Executando a Aplicação

### 1. Subir todos os serviços

Execute o seguinte comando na raiz do projeto:

```bash
docker-compose up -d
```

Este comando irá:
- Criar e iniciar o banco de dados PostgreSQL
- Executar o script SQL `generate_database.sql` automaticamente
- Construir e iniciar o backend Spring Boot
- Construir e iniciar o frontend Vue 3 (servido pelo Nginx)

### 2. Verificar o status dos serviços

```bash
docker-compose ps
```

### 3. Ver os logs

Para ver os logs de todos os serviços:
```bash
docker-compose logs -f
```

Para ver os logs de um serviço específico:
```bash
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

### 4. Acessar a aplicação

Após alguns instantes (aguarde o backend inicializar completamente):
- **Frontend**: http://localhost:5000
- **Backend API**: http://localhost:8081/api

## Comandos Úteis

### Parar os serviços
```bash
docker-compose stop
```

### Parar e remover os containers
```bash
docker-compose down
```

### Parar, remover containers e volumes (limpa dados do banco)
```bash
docker-compose down -v
```

### Reconstruir as imagens
```bash
docker-compose build --no-cache
```

### Reconstruir e subir novamente
```bash
docker-compose up -d --build
```

## Estrutura dos Serviços

### PostgreSQL
- **Porta**: 5432
- **Banco de dados**: wa_db
- **Usuário**: postgres
- **Senha**: postgres
- **Volume**: postgres_data (persiste os dados)

### Backend (Spring Boot)
- **Porta**: 8081
- **Healthcheck**: Verifica se a API está respondendo
- **Dependências**: Aguarda o PostgreSQL estar saudável antes de iniciar

### Frontend (Vue 3 + Nginx)
- **Porta**: 80
- **Proxy**: Requisições `/api` são redirecionadas para o backend
- **Dependências**: Aguarda o backend estar disponível

## Solução de Problemas

### Backend não inicia
1. Verifique os logs: `docker-compose logs backend`
2. Certifique-se de que o PostgreSQL está rodando: `docker-compose ps`
3. Verifique se o banco de dados foi criado corretamente

### Frontend não carrega
1. Verifique os logs: `docker-compose logs frontend`
2. Verifique se o backend está respondendo: `curl http://localhost:8081/api/persons`
3. Verifique se o Nginx está rodando: `docker-compose ps frontend`

### Banco de dados não inicializa
1. Verifique os logs: `docker-compose logs postgres`
2. Certifique-se de que o arquivo `generate_database.sql` existe na raiz do projeto
3. Remova o volume e tente novamente: `docker-compose down -v && docker-compose up -d`

### Porta já está em uso
Se alguma porta estiver em uso, você pode alterar no arquivo `docker-compose.yml`:
- PostgreSQL: altere `5432:5432` para `5433:5432` (ou outra porta)
- Backend: altere `8081:8081` para `8082:8081` (ou outra porta)
- Frontend: altere `5000:80` para `3000:80` (ou outra porta)

## Desenvolvimento

Para desenvolvimento local apenas com o banco de dados Docker:

```bash
docker-compose -f docker-compose.dev.yml up -d
```

Isso iniciará apenas o PostgreSQL, permitindo que você execute o backend e frontend localmente.

## Variáveis de Ambiente

Você pode personalizar as configurações criando um arquivo `.env` na raiz do projeto:

```env
POSTGRES_PASSWORD=sua_senha
SPRING_DATASOURCE_PASSWORD=sua_senha
```

E referenciar no `docker-compose.yml` usando `${POSTGRES_PASSWORD}`.

