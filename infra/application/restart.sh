#!/bin/bash

# Script para reiniciar a aplicação no WSL
# Uso: ./restart.sh

set -e

echo "🔄 Reiniciando aplicação..."

# Usa docker compose (v2) se disponível, caso contrário usa docker-compose (v1)
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

$COMPOSE_CMD restart

echo "✅ Aplicação reiniciada com sucesso!"

