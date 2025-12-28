# Sistema de Gestão de Clientes e Processos

Sistema full-stack para advogados acompanharem seus clientes e processos jurídicos.

## Tecnologias

- **Backend**: Spring Boot 3.2.0, Java 17, PostgreSQL, Apache POI (geração de documentos Word)
- **Frontend**: Vue 3, Vite, Vue Router, Axios

## Estrutura do Projeto

```
wa/
├── backend/          # Aplicação Spring Boot
├── frontend/         # Aplicação Vue 3
└── generate_database.sql  # Script SQL para criar o banco de dados
```

## Pré-requisitos

### Para execução com Docker (Recomendado)
- Docker 20.10+
- Docker Compose 2.0+

### Para execução local
- Java 17 ou superior
- Maven 3.6+
- Node.js 18+ e npm
- PostgreSQL 12+

## Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL chamado `wa_db`:
```sql
CREATE DATABASE wa_db;
```

2. Execute o script SQL para criar as tabelas:
```bash
psql -U postgres -d wa_db -f generate_database.sql
```

Ou execute o script diretamente no PostgreSQL.

## Configuração do Backend

1. Navegue até a pasta do backend:
```bash
cd backend
```

2. Configure as credenciais do banco de dados no arquivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wa_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

3. Compile e execute o backend:
```bash
mvn clean install
mvn spring-boot:run
```

O backend estará disponível em `http://localhost:8081`

## Configuração do Frontend

1. Navegue até a pasta do frontend:
```bash
cd frontend
```

2. Instale as dependências:
```bash
npm install
```

3. Execute o servidor de desenvolvimento:
```bash
npm run dev
```

O frontend estará disponível em `http://localhost:5173`

## Funcionalidades

### Clientes
- Listar todos os clientes
- Visualizar detalhes completos do cliente
- Criar novo cliente
- Editar cliente existente
- Excluir cliente
- **Gerar documentos do cliente** (contratos, procurações, declarações, petições iniciais)

### Processos
- Visualizar processos agrupados por matrícula
- Criar novo processo
- Editar processo existente
- Excluir processo
- **Gerar documentos do processo** (petições, contrarrazões, recursos, etc.)

### Matrículas
- Visualizar matrículas do cliente
- Criar nova matrícula
- Editar matrícula existente
- Excluir matrícula

### Geração de Documentos
O sistema suporta geração automática de documentos Word (.docx) a partir de templates predefinidos:

**Documentos por Processo:**
- Petições específicas por tipo de processo (PISO, INTERNIVEIS, NOVAESCOLA)
- Contrarrazões e recursos
- Embargos de declaração
- Alegações finais
- Apelações
- E outros documentos processuais

**Documentos por Cliente:**
- Contratos (Piso, Interníveis, Nova Escola e combinações)
- Procurações (Piso, Interníveis, Nova Escola e combinações)
- Declarações (ex: Hipossuficiência)
- Petições Iniciais (Interníveis, Nova Escola, Piso VC)

Os templates são organizados em:
- `documents/` - Documentos do cliente (contratos, procurações, declarações)
- `iniciais/` - Petições iniciais por tipo de processo
- Raiz - Documentos processuais específicos por tipo de processo

Os nomes dos documentos são formatados automaticamente para exibição amigável (ex: `Contrato_Interniveis_NovaEscola_Piso.docx` → "Contrato Interníveis Nova Escola Piso").

## API Endpoints

### Clientes (Persons)
- `GET /api/persons` - Lista todos os clientes
- `GET /api/persons/{id}` - Detalhes do cliente
- `POST /api/persons` - Criar cliente
- `PUT /api/persons/{id}` - Atualizar cliente
- `DELETE /api/persons/{id}` - Excluir cliente

### Processos (Processes)
- `GET /api/processes` - Lista todos os processos
- `GET /api/processes?personId={id}` - Processos de um cliente
- `GET /api/processes/{id}` - Detalhes do processo
- `POST /api/processes` - Criar processo
- `PUT /api/processes/{id}` - Atualizar processo
- `DELETE /api/processes/{id}` - Excluir processo

### Matrículas (Matriculations)
- `GET /api/matriculations` - Lista todas as matrículas
- `GET /api/matriculations?personId={id}` - Matrículas de um cliente
- `GET /api/matriculations/{id}` - Detalhes da matrícula
- `POST /api/matriculations` - Criar matrícula
- `PUT /api/matriculations/{id}` - Atualizar matrícula
- `DELETE /api/matriculations/{id}` - Excluir matrícula

### Movimentações (Moviments)
- `GET /api/moviments` - Lista todas as movimentações
- `GET /api/moviments?processId={id}` - Movimentações de um processo
- `GET /api/moviments/{id}` - Detalhes da movimentação
- `POST /api/moviments` - Criar movimentação
- `PUT /api/moviments/{id}` - Atualizar movimentação
- `DELETE /api/moviments/{id}` - Excluir movimentação

### Documentos (Documents)
- `GET /api/documents/templates?processId={id}` - Lista templates disponíveis para um processo específico
- `POST /api/documents/generate` - Gera documento Word para um processo (body: `{processId, templateName}`)
- `GET /api/documents/client-templates?personId={id}` - Lista templates disponíveis para um cliente
- `POST /api/documents/generate-client` - Gera documento Word para um cliente (body: `{personId, templateName}`)

**Tipos de Processo Suportados:**
- `PISO` - Processos relacionados ao Piso Salarial
- `INTERNIVEIS` - Processos relacionados a Interníveis
- `NOVAESCOLA` - Processos relacionados à Nova Escola

## Executando com Docker (Recomendado)

A forma mais fácil de executar a aplicação é usando Docker Compose:

### No WSL (Windows Subsystem for Linux) - Recomendado para Windows

1. Abra o terminal WSL e navegue até o diretório do projeto:
```bash
cd /mnt/c/Users/douglas.paranhos/WA
# ou se copiou para dentro do WSL:
cd ~/WA
```

2. Dê permissão de execução aos scripts:
```bash
chmod +x *.sh
```

3. Execute o script de inicialização:
```bash
./start.sh
```

4. Aguarde alguns instantes para que todos os serviços iniciem

5. Acesse a aplicação em:
   - Frontend: http://localhost:5000
   - Backend API: http://localhost:8081/api

**Scripts disponíveis:**
- `./start.sh` - Inicia a aplicação
- `./stop.sh` - Para a aplicação
- `./restart.sh` - Reinicia a aplicação
- `./logs.sh` - Mostra os logs (ou `./logs.sh backend` para um serviço específico)
- `./clean.sh` - Limpa tudo incluindo volumes e dados do banco

📖 **Veja o guia completo em [WSL.md](WSL.md)**

### No Linux/Mac

1. Certifique-se de que o Docker e Docker Compose estão instalados

2. Execute o comando para subir todos os serviços:
```bash
docker-compose up -d
# ou
docker compose up -d
```

3. Aguarde alguns instantes para que todos os serviços iniciem

4. Acesse a aplicação em:
   - Frontend: http://localhost:5000
   - Backend API: http://localhost:8081/api

5. Para parar os serviços:
```bash
docker-compose down
# ou
docker compose down
```

6. Para parar e remover os volumes (limpar dados do banco):
```bash
docker-compose down -v
```

### Serviços Docker

- **PostgreSQL**: Banco de dados na porta 5432
- **Backend**: API Spring Boot na porta 8081
- **Frontend**: Interface Vue 3 servida pelo Nginx na porta 5000

O script SQL `generate_database.sql` é executado automaticamente na inicialização do PostgreSQL.

## Estrutura de Templates de Documentos

Os templates de documentos estão localizados em `backend/src/main/resources/documents/`:

```
documents/
├── documentos/          # Documentos do cliente (contratos, procurações, declarações)
│   ├── Contrato_*.docx
│   ├── Procuracao_*.docx
│   └── Declaracao_*.docx
├── iniciais/            # Petições iniciais por tipo de processo
│   ├── Peticao_Inicial_Interniveis.docx
│   ├── Peticao_Inicial_Nova_Escola.docx
│   └── Peticao_Inicial_Piso_VC.docx
└── *.docx              # Documentos processuais (petições, recursos, etc.)
```

Os templates usam placeholders que são substituídos automaticamente pelos dados do cliente/processo:
- `{nome}` - Nome completo do cliente
- `{cpf}` - CPF do cliente
- `{rg}` - RG do cliente
- `{endereco}` - Endereço completo
- `{numeroProcesso}` - Número do processo
- E outros campos disponíveis nas entidades

## Observações

- O sistema não possui autenticação na versão inicial
- Os processos são exibidos agrupados por matrícula na página de detalhes do cliente
- O CORS está configurado para permitir requisições do frontend (localhost:5173 e localhost:5000)
- No Docker, o frontend faz proxy das requisições `/api` para o backend automaticamente
- Os documentos gerados são baixados automaticamente pelo navegador
- Os nomes dos templates são formatados automaticamente para exibição amigável na interface

