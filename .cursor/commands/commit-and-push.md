# Commit and Push

Adiciona todos os arquivos ao git, faz commit com mensagem detalhada e faz push.

## Comandos

```bash
# Adicionar todos os arquivos modificados e novos
git add .

# Fazer commit com mensagem detalhada
git commit -m "feat: <descrição detalhada das mudanças>

- <item 1 das mudanças>
- <item 2 das mudanças>
- <item 3 das mudanças>"

# Fazer push para o repositório remoto
git push
```

## Uso

1. Substitua `<descrição detalhada das mudanças>` por uma descrição clara do que foi alterado
2. Liste os itens principais das mudanças nos bullets points
3. Use prefixos convencionais de commit:
   - `feat:` para novas funcionalidades
   - `fix:` para correções de bugs
   - `refactor:` para refatorações
   - `docs:` para documentação
   - `style:` para formatação
   - `test:` para testes
   - `chore:` para tarefas de manutenção

## Exemplo

```bash
git add .
git commit -m "feat: adiciona autenticação de clientes

- Implementa modal de login para clientes
- Adiciona serviço de autenticação no backend
- Cria endpoint para validação de credenciais
- Adiciona geração automática de senhas"
git push
```
