#!/bin/bash

# Script para resetar o banco de dados e aplicar o schema corrigido
# Uso: ./reset-db.sh

set -e

echo "🔄 Resetando banco de dados..."

# Usa docker compose (v2) se disponível, caso contrário usa docker-compose (v1)
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

# Para os serviços
echo "⏹️  Parando serviços..."
$COMPOSE_CMD down

# Remove o volume do PostgreSQL
echo "🗑️  Removendo volume do banco de dados..."
$COMPOSE_CMD down -v

# Reconstrói e inicia os serviços
echo "🚀 Reiniciando serviços com schema corrigido..."
$COMPOSE_CMD up -d --build

echo ""
echo "⏳ Aguardando serviços iniciarem..."
sleep 15

# Verifica o status
echo ""
echo "📊 Status dos serviços:"
$COMPOSE_CMD ps

echo ""
echo "✅ Banco de dados resetado e serviços reiniciados!"
echo ""
echo "🌐 Acesse a aplicação em:"
echo "   - Frontend: http://localhost:5000"
echo "   - Backend API: http://localhost:8081/api"
echo ""

