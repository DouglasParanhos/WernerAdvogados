#!/bin/bash

# Script para parar a aplicação no WSL
# Uso: ./stop.sh

set -e

echo "🛑 Parando aplicação..."

# Usa docker compose (v2) se disponível, caso contrário usa docker-compose (v1)
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

$COMPOSE_CMD down

echo "✅ Aplicação parada com sucesso!"

