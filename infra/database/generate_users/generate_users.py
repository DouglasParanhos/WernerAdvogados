#!/usr/bin/env python3
"""
Script para gerar hashes BCrypt para inserção de usuários no banco de dados.
Lê as credenciais de um arquivo users.config ou de variáveis de ambiente.
"""

import bcrypt
import sys
import os
from pathlib import Path

def generate_bcrypt_hash(password):
    """Gera um hash BCrypt para a senha fornecida."""
    # BCrypt gera um salt automaticamente
    # Usa rounds=12 para compatibilidade com Spring Security
    salt = bcrypt.gensalt(rounds=12)
    hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
    # Converte $2b$ para $2a$ para compatibilidade com Spring Security
    hash_str = hashed.decode('utf-8')
    if hash_str.startswith('$2b$'):
        hash_str = '$2a$' + hash_str[4:]
    return hash_str

def load_users_from_config():
    """Carrega usuários do arquivo de configuração ou variáveis de ambiente."""
    script_dir = Path(__file__).parent
    config_file = script_dir / "users.config"
    
    users = []
    
    # Tenta carregar do arquivo de configuração
    if config_file.exists():
        with open(config_file, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                # Ignora linhas vazias e comentários
                if not line or line.startswith('#'):
                    continue
                
                # Formato: username:password:role
                parts = line.split(':')
                if len(parts) >= 2:
                    username = parts[0].strip()
                    password = parts[1].strip()
                    role = parts[2].strip() if len(parts) > 2 else "USER"
                    
                    # Valida que não é placeholder
                    if password and password != "YOUR_PASSWORD_HERE":
                        users.append({
                            "username": username,
                            "password": password,
                            "role": role
                        })
    
    # Se não encontrou usuários no arquivo, tenta variáveis de ambiente
    if not users:
        # Formato: USER1_USERNAME, USER1_PASSWORD, USER1_ROLE, etc.
        user_num = 1
        while True:
            username = os.getenv(f"USER{user_num}_USERNAME")
            password = os.getenv(f"USER{user_num}_PASSWORD")
            role = os.getenv(f"USER{user_num}_ROLE", "USER")
            
            if not username or not password:
                break
            
            users.append({
                "username": username,
                "password": password,
                "role": role
            })
            user_num += 1
    
    return users

def main():
    users = load_users_from_config()
    
    if not users:
        print("Erro: Nenhum usuário encontrado!", file=sys.stderr)
        print("", file=sys.stderr)
        print("Configure os usuários de uma das seguintes formas:", file=sys.stderr)
        print("", file=sys.stderr)
        print("1. Crie o arquivo users.config baseado em users.config.example", file=sys.stderr)
        print("2. Ou defina variáveis de ambiente:", file=sys.stderr)
        print("   USER1_USERNAME=username1", file=sys.stderr)
        print("   USER1_PASSWORD=senha_segura", file=sys.stderr)
        print("   USER1_ROLE=USER", file=sys.stderr)
        sys.exit(1)
    
    print("-- Script SQL para inserir usuários")
    print("-- Gerado automaticamente")
    print()
    
    for user in users:
        password_hash = generate_bcrypt_hash(user["password"])
        print(f"-- Usuário: {user['username']}")
        print(f"INSERT INTO public.users (username, password, user_role, created_on, modified_on)")
        print(f"VALUES ('{user['username']}', '{password_hash}', '{user['role']}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
        print(f"ON CONFLICT (username) DO UPDATE SET")
        print(f"    password = EXCLUDED.password,")
        print(f"    modified_on = CURRENT_TIMESTAMP;")
        print()

if __name__ == "__main__":
    try:
        main()
    except ImportError:
        print("Erro: Biblioteca bcrypt não encontrada.", file=sys.stderr)
        print("Instale com: pip install bcrypt", file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(f"Erro: {e}", file=sys.stderr)
        sys.exit(1)
