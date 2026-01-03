# Execute Tests

Cria testes para funcionalidades recém criadas e executa todos os testes (backend e frontend) para garantir que tudo está funcionando.

## Passos do Comando

1. **Verificar funcionalidades recém criadas** que precisam de testes
2. **Criar testes unitários** para novas funcionalidades se necessário
3. **Executar todos os testes** do backend (Maven/JUnit)
4. **Executar todos os testes** do frontend (Vitest)
5. **Verificar se todos os testes passam** e reportar resultados

## Comandos

### Executar Todos os Testes

```bash
# Executar testes do backend
cd backend
mvn clean test

# Executar testes do frontend
cd ../frontend
npm test
```

### Executar em Sequência (Windows PowerShell)

```powershell
# Executar ambos em sequência
cd backend; mvn clean test; cd ../frontend; npm test
```

### Executar em Sequência (Linux/Mac)

```bash
# Executar ambos em sequência
cd backend && mvn clean test && cd ../frontend && npm test
```

## Estrutura de Testes

### Backend
- **Localização**: `backend/src/test/java/com/wa/`
- **Framework**: JUnit 5 + Mockito
- **Executar**: `mvn test`
- **Teste específico**: `mvn test -Dtest=NomeDaClasseTest`
- **Com relatório**: `mvn test surefire-report:report`

### Frontend
- **Localização**: `frontend/src/**/__tests__/`
- **Framework**: Vitest + Vue Test Utils
- **Executar**: `npm test` ou `npx vitest run`
- **Modo watch**: `npm run test:watch` ou `npx vitest`
- **Com coverage**: `npm run test:coverage` ou `npx vitest --coverage`

## Exemplo de Execução Completa

```bash
# 1. Executar testes do backend
cd backend
mvn clean test

# Verificar se passou (exit code 0)
if [ $? -eq 0 ]; then
    echo "✅ Testes do backend passaram!"
    
    # 2. Executar testes do frontend
    cd ../frontend
    npm test
    
    if [ $? -eq 0 ]; then
        echo "✅ Testes do frontend passaram!"
        echo "✅ Todos os testes passaram com sucesso!"
    else
        echo "❌ Alguns testes do frontend falharam"
        exit 1
    fi
else
    echo "❌ Alguns testes do backend falharam"
    exit 1
fi
```

## Verificação de Cobertura

Para verificar a cobertura de testes:

```bash
# Backend (se JaCoCo estiver configurado)
cd backend
mvn test jacoco:report
# Relatório em: target/site/jacoco/index.html

# Frontend
cd frontend
npm run test:coverage
# Relatório no terminal e em: coverage/
```

## Funcionalidades que Precisam de Testes

Ao criar uma nova funcionalidade, certifique-se de criar testes para:

- **Backend**:
  - Controllers (endpoints REST)
  - Services (lógica de negócio)
  - DTOs (validações)
  - Configurações (deserializadores, etc.)

- **Frontend**:
  - Services (chamadas de API)
  - Components (comportamento e interações)
  - Views (fluxos de usuário)

## Notas Importantes

- ✅ Certifique-se de que o banco de dados de teste está configurado corretamente
- ✅ Testes do backend podem precisar de um banco PostgreSQL em execução
- ✅ Testes do frontend são executados em ambiente Node.js isolado (jsdom)
- ✅ Se algum teste falhar, corrija o código ou o teste antes de fazer commit
- ✅ Mantenha a cobertura de testes acima de 70% quando possível

## Troubleshooting

### Backend: Erro de conexão com banco
- Verifique se o PostgreSQL está rodando
- Confirme as configurações em `application.properties`

### Frontend: Erro de módulo não encontrado
- Execute `npm install` para instalar dependências
- Verifique se `vitest` e `@vue/test-utils` estão instalados

### Testes falhando após mudanças
- Execute `mvn clean test` no backend para limpar cache
- Execute `npm test` no frontend para verificar erros

--- End Command ---

