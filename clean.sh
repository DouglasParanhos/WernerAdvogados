#!/bin/bash

# Script para limpar completamente a aplicação no WSL
# Uso: ./clean.sh
# ATENÇÃO: Isso remove todos os containers, volumes e dados do banco!

set -e

echo "⚠️  ATENÇÃO: Isso irá remover todos os containers, volumes e dados do banco!"
read -p "Tem certeza que deseja continuar? (s/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Ss]$ ]]; then
    echo "❌ Operação cancelada."
    exit 1
fi

echo "🧹 Limpando aplicação..."

# Usa docker compose (v2) se disponível, caso contrário usa docker-compose (v1)
if docker compose version &> /dev/null; then
    COMPOSE_CMD="docker compose"
else
    COMPOSE_CMD="docker-compose"
fi

$COMPOSE_CMD down -v

echo "✅ Limpeza concluída!"

