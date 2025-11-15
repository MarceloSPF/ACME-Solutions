# 📋 Lista Completa de Endpoints REST - ACME Solutions Workshop

**Base URL:** `http://localhost:8080/api`

---

## 1. 👥 Customers (Clientes)

**Base Path:** `/api/customers`

| Método | Endpoint | Descrição | Body |
|--------|----------|-----------|------|
| `GET` | `/api/customers` | Listar todos os clientes | - |
| `GET` | `/api/customers/{id}` | Buscar cliente por ID | - |
| `GET` | `/api/customers/search?name={name}` | Buscar clientes por nome | - |
| `POST` | `/api/customers` | Criar novo cliente | `CustomerDTO` |
| `PUT` | `/api/customers/{id}` | Atualizar cliente | `CustomerDTO` |
| `DELETE` | `/api/customers/{id}` | Deletar cliente | - |

### Exemplo de Request Body (POST/PUT):
```json
{
  "name": "João Silva",
  "email": "joao@email.com",
  "phone": "(11) 99999-9999",
  "address": "Rua Exemplo, 123"
}
```

---

## 2. 🚗 Vehicles (Veículos)

**Base Path:** `/api/vehicles`

| Método | Endpoint | Descrição | Body |
|--------|----------|-----------|------|
| `GET` | `/api/vehicles` | Listar todos os veículos | - |
| `GET` | `/api/vehicles/{id}` | Buscar veículo por ID | - |
| `GET` | `/api/vehicles/customer/{customerId}` | Listar veículos de um cliente | - |
| `POST` | `/api/vehicles` | Criar novo veículo | `VehicleDTO` |

### Exemplo de Request Body (POST):
```json
{
  "brand": "Toyota",
  "model": "Corolla",
  "modelYear": 2020,
  "licensePlate": "ABC-1234",
  "customerId": 1
}
```

---

## 3. 🔧 Technicians (Técnicos)

**Base Path:** `/api/technicians`

| Método | Endpoint | Descrição | Body |
|--------|----------|-----------|------|
| `GET` | `/api/technicians` | Listar todos os técnicos | - |
| `GET` | `/api/technicians/{id}` | Buscar técnico por ID | - |
| `GET` | `/api/technicians/search/specialization?specialization={spec}` | Buscar técnicos por especialização | - |
| `POST` | `/api/technicians` | Criar novo técnico | `TechnicianDTO` |
| `PUT` | `/api/technicians/{id}` | Atualizar técnico | `TechnicianDTO` |
| `DELETE` | `/api/technicians/{id}` | Deletar técnico | - |

### Exemplo de Request Body (POST/PUT):
```json
{
  "name": "Carlos Mecânico",
  "email": "carlos@oficina.com",
  "specialization": "Motor"
}
```

---

## 4. 📋 Service Orders (Ordens de Serviço)

**Base Path:** `/api/service-orders`

| Método | Endpoint | Descrição | Body |
|--------|----------|-----------|------|
| `GET` | `/api/service-orders` | Listar todas as ordens de serviço | - |
| `GET` | `/api/service-orders/customer/{customerId}` | Listar ordens de um cliente | - |
| `GET` | `/api/service-orders/technician/{technicianId}` | Listar ordens de um técnico | - |
| `POST` | `/api/service-orders` | Criar nova ordem de serviço | `ServiceOrderRequestDTO` |
| `PUT` | `/api/service-orders/{id}/status` | Atualizar status da ordem | `ServiceStatus` |

### Exemplo de Request Body (POST):
```json
{
  "customerId": 1,
  "vehicleId": 1,
  "technicianId": 1,
  "description": "Revisão completa do veículo"
}
```

### Exemplo de Request Body (PUT - Status):
```json
"IN_PROGRESS"
```
**Status válidos:** `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELED`

---

## 5. 🔩 Parts (Peças)

**Base Path:** `/api/parts`

| Método | Endpoint | Descrição | Body |
|--------|----------|-----------|------|
| `GET` | `/api/parts` | Listar todas as peças | - |
| `GET` | `/api/parts/{id}` | Buscar peça por ID | - |
| `GET` | `/api/parts/code/{code}` | Buscar peça por código | - |
| `POST` | `/api/parts` | Criar nova peça | `PartDTO` |
| `PUT` | `/api/parts/{id}` | Atualizar peça | `PartDTO` |
| `PUT` | `/api/parts/{id}/stock?quantity={qty}` | Atualizar estoque da peça | - |
| `DELETE` | `/api/parts/{id}` | Deletar peça | - |

### Exemplo de Request Body (POST/PUT):
```json
{
  "name": "Filtro de óleo",
  "code": "FIL-001",
  "unitPrice": 25.50,
  "stock": 100
}
```

### Exemplo de Atualização de Estoque:
```
PUT /api/parts/1/stock?quantity=50
```
**Nota:** O `quantity` é a quantidade a ser **reduzida** do estoque.

---

## 6. 🛠️ Service Items (Itens de Serviço)

**Base Path:** `/api/service-items`

| Método | Endpoint | Descrição | Body |
|--------|----------|-----------|------|
| `GET` | `/api/service-items/{id}` | Buscar item de serviço por ID | - |
| `GET` | `/api/service-items/order/{orderId}` | Listar itens de uma ordem de serviço | - |
| `POST` | `/api/service-items` | Adicionar item a uma ordem | `ServiceItemDTO` |
| `PUT` | `/api/service-items/{id}` | Atualizar item de serviço | `ServiceItemDTO` |
| `DELETE` | `/api/service-items/{id}` | Remover item de serviço | - |

### Exemplo de Request Body (POST/PUT):
```json
{
  "serviceOrderId": 1,
  "description": "Troca de óleo",
  "laborCost": 50.00,
  "quantity": 1
}
```

**Nota:** O custo total da ordem é **atualizado automaticamente** quando itens são adicionados, atualizados ou removidos.

---

## 7. 🔗 Service Order Parts (Peças em Ordens de Serviço)

**Base Path:** `/api/service-orders/{orderId}/parts`

| Método | Endpoint | Descrição | Parâmetros |
|--------|----------|-----------|------------|
| `GET` | `/api/service-orders/{orderId}/parts` | Listar peças de uma ordem | `orderId` (path) |
| `POST` | `/api/service-orders/{orderId}/parts?partId={id}&quantity={qty}` | Adicionar peça à ordem | `orderId` (path), `partId` (query), `quantity` (query) |
| `PUT` | `/api/service-orders/{orderId}/parts/{orderPartId}/quantity?quantity={qty}` | Atualizar quantidade de peça | `orderId` (path), `orderPartId` (path), `quantity` (query) |
| `DELETE` | `/api/service-orders/{orderId}/parts/{orderPartId}` | Remover peça da ordem | `orderId` (path), `orderPartId` (path) |

### Exemplo de Adicionar Peça:
```
POST /api/service-orders/1/parts?partId=1&quantity=2
```

### Exemplo de Atualizar Quantidade:
```
PUT /api/service-orders/1/parts/1/quantity?quantity=3
```

**Funcionalidades:**
- ✅ Verifica estoque antes de adicionar
- ✅ Atualiza estoque automaticamente
- ✅ Snapshot do preço no momento da venda
- ✅ Atualiza custo total da ordem automaticamente
- ✅ Devolve ao estoque ao remover peça

---

## 📊 Resumo por Controller

| Controller | Total de Endpoints |
|------------|-------------------|
| **CustomerController** | 6 endpoints |
| **VehicleController** | 4 endpoints |
| **TechnicianController** | 6 endpoints |
| **ServiceOrderController** | 5 endpoints |
| **PartController** | 7 endpoints |
| **ServiceItemController** | 5 endpoints |
| **ServiceOrderPartController** | 4 endpoints |
| **TOTAL** | **37 endpoints** |

---

## 🧪 Exemplos de Teste com cURL

### 1. Criar Cliente
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "phone": "(11) 99999-9999",
    "address": "Rua Exemplo, 123"
  }'
```

### 2. Criar Veículo
```bash
curl -X POST http://localhost:8080/api/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "brand": "Toyota",
    "model": "Corolla",
    "modelYear": 2020,
    "licensePlate": "ABC-1234",
    "customerId": 1
  }'
```

### 3. Criar Técnico
```bash
curl -X POST http://localhost:8080/api/technicians \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Carlos Mecânico",
    "email": "carlos@oficina.com",
    "specialization": "Motor"
  }'
```

### 4. Criar Ordem de Serviço
```bash
curl -X POST http://localhost:8080/api/service-orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "vehicleId": 1,
    "technicianId": 1,
    "description": "Revisão completa"
  }'
```

### 5. Criar Peça
```bash
curl -X POST http://localhost:8080/api/parts \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Filtro de óleo",
    "code": "FIL-001",
    "unitPrice": 25.50,
    "stock": 100
  }'
```

### 6. Adicionar Item de Serviço
```bash
curl -X POST http://localhost:8080/api/service-items \
  -H "Content-Type: application/json" \
  -d '{
    "serviceOrderId": 1,
    "description": "Troca de óleo",
    "laborCost": 50.00,
    "quantity": 1
  }'
```

### 7. Adicionar Peça à Ordem
```bash
curl -X POST "http://localhost:8080/api/service-orders/1/parts?partId=1&quantity=2"
```

### 8. Atualizar Status da Ordem
```bash
curl -X PUT http://localhost:8080/api/service-orders/1/status \
  -H "Content-Type: application/json" \
  -d '"IN_PROGRESS"'
```

---

## 🔄 Fluxo Completo de Teste

### 1. Setup Inicial
```bash
# 1. Criar cliente
POST /api/customers

# 2. Criar veículo (vinculado ao cliente)
POST /api/vehicles

# 3. Criar técnico
POST /api/technicians

# 4. Criar peça
POST /api/parts
```

### 2. Criar Ordem de Serviço
```bash
# 5. Criar ordem de serviço
POST /api/service-orders
```

### 3. Adicionar Itens e Peças
```bash
# 6. Adicionar item de serviço
POST /api/service-items

# 7. Adicionar peça à ordem
POST /api/service-orders/{orderId}/parts?partId={id}&quantity={qty}
```

### 4. Verificar Custo Total
```bash
# 8. Buscar ordem (custo total calculado automaticamente)
GET /api/service-orders/{id}
```

### 5. Atualizar Status
```bash
# 9. Atualizar status da ordem
PUT /api/service-orders/{id}/status
```

---

## 📝 Notas Importantes

1. **Cálculo Automático de Custo:**
   - O `totalCost` da ordem é calculado automaticamente
   - Soma de todos os itens de serviço (laborCost * quantity)
   - Soma de todas as peças (unitPrice * quantity)

2. **Gerenciamento de Estoque:**
   - Estoque é verificado antes de adicionar peças
   - Estoque é atualizado automaticamente
   - Peças são devolvidas ao estoque ao remover da ordem

3. **Validações:**
   - Todos os endpoints POST/PUT validam dados de entrada
   - Retornam erro 400 (Bad Request) se dados inválidos
   - Retornam erro 404 (Not Found) se recurso não encontrado

4. **CORS:**
   - Configurado para aceitar requisições de `http://localhost:3000`
   - Métodos permitidos: GET, POST, PUT, DELETE, PATCH, OPTIONS

---

## 🎯 Endpoints Mais Utilizados

### Para Teste Rápido:
1. `GET /api/customers` - Listar clientes
2. `GET /api/service-orders` - Listar ordens
3. `POST /api/service-orders` - Criar ordem
4. `POST /api/service-items` - Adicionar item
5. `POST /api/service-orders/{id}/parts` - Adicionar peça
6. `GET /api/service-orders/{id}` - Ver ordem com custo calculado

---

**Total: 37 endpoints REST disponíveis para teste!** 🚀

