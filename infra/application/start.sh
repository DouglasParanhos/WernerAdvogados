#!/bin/bash

# Script para iniciar a aplicação no WSL
# Uso: ./start.sh

set -e

echo "🚀 Iniciando aplicação no WSL..."
echo ""

# Verifica se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Por favor, inicie o Docker Desktop ou Docker daemon."
    exit 1
fi

echo "✅ Docker está rodando"
echo ""

# Verifica se o docker-compose está disponível
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ docker-compose não encontrado. Por favor, instale o docker-compose."
    exit 1
fi

# Usa docker compose (v2) se disponível, caso contrário usa docker-compose (v1)
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

echo "📦 Construindo e iniciando os containers..."
echo ""

# Constrói e inicia os serviços
$COMPOSE_CMD up -d --build

echo ""
echo "⏳ Aguardando serviços iniciarem..."
sleep 10

# Verifica o status dos serviços
echo ""
echo "📊 Status dos serviços:"
$COMPOSE_CMD ps

echo ""
echo "✅ Aplicação iniciada com sucesso!"
echo ""
echo "🌐 Acesse a aplicação em:"
echo "   - Frontend: http://localhost:5000"
echo "   - Backend API: http://localhost:8081/api"
echo ""
echo "📝 Para ver os logs:"
echo "   $COMPOSE_CMD logs -f"
echo ""
echo "🛑 Para parar a aplicação:"
echo "   $COMPOSE_CMD down"
echo ""

