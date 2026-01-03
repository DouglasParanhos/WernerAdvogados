# Stop, Rebuild e Restart Container

Para, faz rebuild sem cache e reinicia o(s) container(es).

## Comandos

```bash
# Parar o container
docker-compose stop <container>

# Remover o container
docker-compose rm -f <container>

# Rebuild sem cache
docker-compose build --no-cache <container>

# Reiniciar o container
docker-compose up -d <container>
```

## Uso

- Substitua `<container>` pelo nome do serviço (ex: `backend`, `frontend`)
- Se nenhum container for especificado, execute os comandos para `backend` e `frontend`
