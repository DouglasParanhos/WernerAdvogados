#!/bin/bash

# Script para ver os logs da aplicação no WSL
# Uso: ./logs.sh [servico]
# Exemplo: ./logs.sh backend

set -e

# Usa docker compose (v2) se disponível, caso contrário usa docker-compose (v1)
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

if [ -z "$1" ]; then
    echo "📋 Mostrando logs de todos os serviços..."
    echo "   (Pressione Ctrl+C para sair)"
    echo ""
    $COMPOSE_CMD logs -f
else
    echo "📋 Mostrando logs do serviço: $1"
    echo "   (Pressione Ctrl+C para sair)"
    echo ""
    $COMPOSE_CMD logs -f "$1"
fi

