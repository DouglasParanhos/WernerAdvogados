#!/usr/bin/env python3
"""
Script para cadastrar cliente via API
Uso: python cadastrar_cliente.py
"""

import requests
import json
from datetime import datetime

# Configurações da API
BASE_URL = "https://frontend-production-48d1.up.railway.app/api"
# Ou use: BASE_URL = "http://localhost:8081/api" para desenvolvimento local

def login(username, password):
    """Faz login e retorna o token"""
    response = requests.post(
        f"{BASE_URL}/auth/login",
        json={"username": username, "password": password}
    )
    if response.status_code == 200:
        return response.json().get("token")
    else:
        raise Exception(f"Erro no login: {response.status_code} - {response.text}")

def format_date_to_iso(date_str):
    """Converte data dd/mm/yyyy para formato ISO"""
    day, month, year = date_str.split('/')
    return f"{year}-{month}-{day}T00:00:00"

def cadastrar_cliente(token):
    """Cadastra o cliente Eva Maria Magdalena Donato Baptista"""
    
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    # Dados do cliente
    cliente_data = {
        "fullname": "Eva Maria Magdalena Donato Baptista",
        "email": None,  # Opcional
        "cpf": "093.351.567-79",
        "rg": "81.020.485-7",
        "estadoCivil": "CASADO",
        "dataNascimento": format_date_to_iso("21/08/1948"),
        "profissao": "aposentada",
        "telefone": "21 996932833",
        "vivo": True,
        "representante": None,
        "idFuncional": "3928471-9",
        "nacionalidade": "Brasileira",
        "address": {
            "logradouro": "Rua Alexandre Lima, nº 10, Lote 191, Nova Cidade",
            "cidade": "São Gonçalo",
            "estado": "RJ",
            "cep": "24455-770"
        },
        "matriculation1": {
            "numero": "0230183-6",
            "cargo": "DOCENTE II",
            "inicioErj": format_date_to_iso("01/03/1983"),
            "dataAposentadoria": format_date_to_iso("10/06/2009"),
            "nivelAtual": "7",
            "trienioAtual": "45",
            "referencia": "B"
            # personId não é necessário na criação
        },
        "matriculation2": None  # Não há segunda matrícula
    }
    
    response = requests.post(
        f"{BASE_URL}/persons",
        headers=headers,
        json=cliente_data
    )
    
    if response.status_code in [200, 201]:
        print("✅ Cliente cadastrado com sucesso!")
        print(json.dumps(response.json(), indent=2, ensure_ascii=False))
        return response.json()
    else:
        print(f"❌ Erro ao cadastrar cliente: {response.status_code}")
        print(response.text)
        raise Exception(f"Erro: {response.status_code} - {response.text}")

if __name__ == "__main__":
    print("=== Cadastro de Cliente ===")
    print("Cliente: Eva Maria Magdalena Donato Baptista\n")
    
    # Solicitar credenciais
    username = input("Digite o usuário: ")
    password = input("Digite a senha: ")
    
    try:
        print("\n🔐 Fazendo login...")
        token = login(username, password)
        print("✅ Login realizado com sucesso!\n")
        
        print("📝 Cadastrando cliente...")
        cadastrar_cliente(token)
        
    except Exception as e:
        print(f"\n❌ Erro: {e}")


