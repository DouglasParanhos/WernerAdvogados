# Guia de Execução no WSL (Windows Subsystem for Linux)

Este guia explica como executar a aplicação usando Docker no WSL.

## Pré-requisitos

### 1. Instalar WSL

Se você ainda não tem o WSL instalado:

```powershell
# No PowerShell como Administrador
wsl --install
```

Ou instale uma distribuição específica:
```powershell
wsl --install -d Ubuntu
```

### 2. Instalar Docker no WSL

#### Opção A: Docker Desktop (Recomendado)

1. Instale o [Docker Desktop para Windows](https://www.docker.com/products/docker-desktop/)
2. No Docker Desktop, vá em Settings > General
3. Marque "Use the WSL 2 based engine"
4. Vá em Settings > Resources > WSL Integration
5. Ative a integração com sua distribuição WSL

#### Opção B: Docker Engine no WSL

```bash
# No terminal WSL
sudo apt update
sudo apt install docker.io docker-compose
sudo usermod -aG docker $USER

# Reinicie o WSL ou faça logout/login
```

## Configuração Inicial

### 1. Abra o terminal WSL

Abra o terminal da sua distribuição WSL (Ubuntu, Debian, etc.)

### 2. Navegue até o diretório do projeto

```bash
# Se o projeto está em C:\Users\douglas.paranhos\WA
cd /mnt/c/Users/douglas.paranhos/WA

# Ou se você copiou o projeto para dentro do WSL
cd ~/WA
```

### 3. Dê permissão de execução aos scripts

```bash
chmod +x *.sh
```

## Executando a Aplicação

### Método 1: Usando os Scripts (Recomendado)

#### Iniciar a aplicação:
```bash
./start.sh
```

#### Parar a aplicação:
```bash
./stop.sh
```

#### Reiniciar a aplicação:
```bash
./restart.sh
```

#### Ver logs:
```bash
# Todos os serviços
./logs.sh

# Serviço específico
./logs.sh backend
./logs.sh frontend
./logs.sh postgres
```

#### Limpar tudo (remove volumes e dados):
```bash
./clean.sh
```

### Método 2: Usando Docker Compose Diretamente

#### Iniciar:
```bash
docker-compose up -d
# ou
docker compose up -d
```

#### Parar:
```bash
docker-compose down
# ou
docker compose down
```

#### Ver logs:
```bash
docker-compose logs -f
# ou
docker compose logs -f
```

#### Reconstruir e iniciar:
```bash
docker-compose up -d --build
# ou
docker compose up -d --build
```

## Verificando o Status

### Verificar se os containers estão rodando:
```bash
docker-compose ps
# ou
docker compose ps
```

### Verificar logs de um serviço específico:
```bash
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres
```

### Verificar uso de recursos:
```bash
docker stats
```

## Acessando a Aplicação

Após iniciar a aplicação, aguarde alguns instantes e acesse:

- **Frontend**: http://localhost:5000 (ou http://127.0.0.1:5000)
- **Backend API**: http://localhost:8081/api

## Solução de Problemas

### Docker não está rodando

```bash
# Verificar se o Docker está rodando
docker info

# Se não estiver, inicie o Docker Desktop no Windows
# Ou inicie o serviço Docker no WSL:
sudo service docker start
```

### Erro de permissão

```bash
# Adicionar usuário ao grupo docker
sudo usermod -aG docker $USER

# Fazer logout e login novamente, ou executar:
newgrp docker
```

### Porta já está em uso

Se alguma porta estiver em uso, você pode:

1. Parar o serviço que está usando a porta
2. Ou alterar as portas no `docker-compose.yml`

### Problemas com caminhos do Windows

Se você está acessando arquivos do Windows (`/mnt/c/...`), pode haver problemas de performance. Recomenda-se:

1. Copiar o projeto para dentro do WSL:
```bash
cp -r /mnt/c/Users/douglas.paranhos/WA ~/WA
cd ~/WA
```

2. Ou usar o WSL2 que tem melhor performance com arquivos do Windows

### Limpar tudo e começar do zero

```bash
# Parar e remover tudo
docker-compose down -v

# Remover imagens (opcional)
docker rmi wa-backend wa-frontend

# Reconstruir e iniciar
docker-compose up -d --build
```

## Dicas Úteis

### Acessar o banco de dados diretamente

```bash
docker-compose exec postgres psql -U postgres -d wa_db
```

### Acessar o shell do container backend

```bash
docker-compose exec backend sh
```

### Acessar o shell do container frontend

```bash
docker-compose exec frontend sh
```

### Ver variáveis de ambiente de um container

```bash
docker-compose exec backend env
```

## Performance no WSL

Para melhor performance:

1. **Use WSL2** (não WSL1)
2. **Mantenha os arquivos dentro do WSL** (não em `/mnt/c/`)
3. **Use Docker Desktop** com integração WSL2 habilitada

## Comandos Rápidos

```bash
# Iniciar
./start.sh

# Ver logs
./logs.sh

# Parar
./stop.sh

# Status
docker-compose ps

# Limpar tudo
./clean.sh
```

