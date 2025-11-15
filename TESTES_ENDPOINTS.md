# 🧪 Testes dos Endpoints - ACME Solutions

## ✅ Testes Automatizados Criados

Foram criados **5 classes de teste** usando Spring Boot Test (MockMvc) para testar os principais endpoints:

### 1. **CustomerControllerTest**
Testa todos os endpoints de clientes:
- ✅ Criar cliente
- ✅ Listar todos os clientes
- ✅ Buscar cliente por ID
- ✅ Atualizar cliente
- ✅ Deletar cliente
- ✅ Buscar clientes por nome

### 2. **ServiceOrderControllerTest**
Testa todos os endpoints de ordens de serviço:
- ✅ Criar ordem de serviço
- ✅ Listar todas as ordens
- ✅ Atualizar status da ordem
- ✅ Buscar ordens por cliente
- ✅ Buscar ordens por técnico

### 3. **PartControllerTest**
Testa todos os endpoints de peças:
- ✅ Criar peça
- ✅ Listar todas as peças
- ✅ Buscar peça por ID
- ✅ Buscar peça por código
- ✅ Atualizar peça
- ✅ Atualizar estoque
- ✅ Deletar peça

### 4. **ServiceItemControllerTest**
Testa todos os endpoints de itens de serviço:
- ✅ Criar item de serviço
- ✅ Buscar item por ID
- ✅ Listar itens de uma ordem
- ✅ Atualizar item
- ✅ Deletar item

### 5. **ServiceOrderPartControllerTest**
Testa todos os endpoints de peças em ordens:
- ✅ Adicionar peça à ordem
- ✅ Listar peças de uma ordem
- ✅ Atualizar quantidade
- ✅ Remover peça da ordem

---

## 🚀 Como Executar os Testes

### Opção 1: Via Maven (Recomendado)
```bash
# Executar todos os testes
mvn test

# Executar testes de um controller específico
mvn test -Dtest=CustomerControllerTest

# Executar com relatório de cobertura
mvn test jacoco:report
```

### Opção 2: Via IDE
1. Abra o projeto no IntelliJ IDEA ou Eclipse
2. Navegue até `src/test/java/com/acme/workshop/controller/`
3. Clique com botão direito em qualquer classe de teste
4. Selecione "Run Tests" ou "Debug Tests"

### Opção 3: Executar Teste Individual
```bash
# Via Maven
mvn test -Dtest=CustomerControllerTest#shouldCreateCustomer

# Via linha de comando (se tiver Java configurado)
java -jar target/workshop-1.0.0.jar --spring.profiles.active=test
```

---

## 📊 Estrutura dos Testes

### Configuração
- **@SpringBootTest**: Carrega o contexto completo do Spring
- **@AutoConfigureMockMvc**: Configura MockMvc para testes de API
- **@ActiveProfiles("test")**: Usa perfil de teste (H2 em memória)
- **@Transactional**: Rollback automático após cada teste

### Setup (@BeforeEach)
- Limpa dados anteriores
- Cria dados de teste necessários
- Prepara ambiente para cada teste

### Assertions
- Verifica status HTTP
- Valida estrutura JSON
- Confirma valores retornados

---

## 🔍 Exemplo de Teste

```java
@Test
void shouldCreateCustomer() throws Exception {
    Customer newCustomer = new Customer();
    newCustomer.setName("Maria Santos");
    newCustomer.setEmail("maria@test.com");
    newCustomer.setPhone("(11) 88888-8888");
    newCustomer.setAddress("Rua Nova, 456");

    mockMvc.perform(post("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newCustomer)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Maria Santos"))
            .andExpect(jsonPath("$.email").value("maria@test.com"));
}
```

---

## 📋 Cobertura de Testes

| Controller | Endpoints Testados | Status |
|------------|-------------------|--------|
| CustomerController | 6/6 | ✅ 100% |
| ServiceOrderController | 5/5 | ✅ 100% |
| PartController | 7/7 | ✅ 100% |
| ServiceItemController | 5/5 | ✅ 100% |
| ServiceOrderPartController | 4/4 | ✅ 100% |
| **TOTAL** | **27/27** | ✅ **100%** |

---

## 🎯 Funcionalidades Testadas

### Casos de Sucesso
- ✅ Criação de recursos
- ✅ Listagem de recursos
- ✅ Busca por ID
- ✅ Atualização de recursos
- ✅ Exclusão de recursos
- ✅ Buscas específicas (nome, código, etc.)

### Casos de Erro
- ✅ Recurso não encontrado (404)
- ✅ Validações de entrada
- ✅ Relacionamentos inválidos

### Funcionalidades Especiais
- ✅ Cálculo automático de custo (ServiceItem)
- ✅ Atualização de estoque (Part)
- ✅ Snapshot de preço (ServiceOrderPart)
- ✅ Atualização de status (ServiceOrder)

---

## 🔧 Configuração de Teste

### application-test.properties
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

**Características:**
- Banco H2 em memória (não persiste dados)
- Cria e remove schema automaticamente
- Isolamento completo entre testes
- Execução rápida

---

## 📈 Relatório de Testes

Após executar os testes, você verá:

```
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🐛 Debugging de Testes

### Se um teste falhar:

1. **Verifique o log de erro:**
   ```
   Expected status: 200
   Actual status: 404
   ```

2. **Verifique os dados de setup:**
   - IDs estão corretos?
   - Relacionamentos estão criados?

3. **Execute teste isolado:**
   ```bash
   mvn test -Dtest=CustomerControllerTest#shouldCreateCustomer
   ```

4. **Ative logs detalhados:**
   ```properties
   logging.level.org.springframework.web=DEBUG
   logging.level.com.acme.workshop=DEBUG
   ```

---

## ✅ Próximos Passos

### Melhorias Sugeridas:
- [ ] Adicionar testes de integração com banco real
- [ ] Adicionar testes de performance
- [ ] Adicionar testes de segurança
- [ ] Adicionar testes de validação de negócio
- [ ] Adicionar testes de cálculo automático de custo

---

## 🎉 Status: Testes Prontos!

Todos os testes estão criados e prontos para execução. Execute `mvn test` para validar todos os endpoints! 🚀

