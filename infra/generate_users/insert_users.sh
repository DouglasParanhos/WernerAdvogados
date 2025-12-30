#!/bin/bash
# Script para inserir usuários no banco de dados
# Gera os hashes BCrypt e executa o SQL no container PostgreSQL

# Obtém o diretório do script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "Gerando hashes BCrypt e criando script SQL..."

# Verifica se o Python está disponível
if command -v python3 &> /dev/null; then
    python3 generate_users.py > insert_users.sql
elif command -v python &> /dev/null; then
    python generate_users.py > insert_users.sql
else
    echo "Erro: Python não encontrado. Instale Python e a biblioteca bcrypt:"
    echo "pip install bcrypt"
    exit 1
fi

echo "Script SQL gerado: insert_users.sql"
echo ""
echo "Executando no banco de dados..."

# Executa o SQL no container PostgreSQL
docker exec -i wa-postgres psql -U postgres -d wa_db < insert_users.sql

echo ""
echo "Usuários inseridos com sucesso!"

