# Padrões em Frameworks Modernos - ACME Solutions

Este documento demonstra o uso de padrões de projeto modernos implementados no sistema de gestão da oficina mecânica.

## 1. Dependency Injection (DI)

O Spring Framework implementa o padrão de Injeção de Dependência de forma nativa. Este padrão permite um baixo acoplamento entre classes e facilita os testes unitários.

### 1.1 Injeção via Construtor (Recomendada)

```java
@RestController
@RequestMapping("/api/service-orders")
public class ServiceOrderController {
    
    private final ServiceOrderService serviceOrderService;
    
    // Injeção via construtor - Abordagem recomendada
    public ServiceOrderController(ServiceOrderService serviceOrderService) {
        this.serviceOrderService = serviceOrderService;
    }
    
    @PostMapping
    public ResponseEntity<ServiceOrder> create(@RequestBody ServiceOrderDTO dto) {
        return ResponseEntity.ok(serviceOrderService.create(dto));
    }
}
```

Benefícios da Injeção via Construtor:
- Dependências obrigatórias são explícitas
- Facilita testes unitários
- Imutabilidade das dependências
- Evita problemas com dependências circulares

### 1.2 Injeção via @Autowired (Alternativa)

```java
@Service
public class ServiceOrderService {
    
    @Autowired
    private ServiceOrderRepository repository;
    
    @Autowired
    private CustomerService customerService;
    
    public ServiceOrder findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Service Order not found"));
    }
}
```

Obs: Embora funcional, a injeção via @Autowired em campos não é a abordagem recomendada devido a:
- Dificulta testes unitários
- Permite dependências nulas
- Torna o código mais difícil de manter

## 2. Padrão Repository

O Spring Data JPA implementa o padrão Repository de forma elegante através de interfaces.

### 2.1 Interface Base JpaRepository

```java
@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    // JpaRepository já fornece métodos básicos como:
    // - save(entity)
    // - findById(id)
    // - findAll()
    // - delete(entity)
    // - count()
    // etc.
    
    // Métodos customizados usando convenção de nomes
    List<ServiceOrder> findByCustomerId(Long customerId);
    
    List<ServiceOrder> findByStatus(ServiceStatus status);
    
    List<ServiceOrder> findByTechnicianIdAndStatus(Long technicianId, ServiceStatus status);
    
    // Consulta customizada com @Query
    @Query("SELECT so FROM ServiceOrder so WHERE so.createdAt BETWEEN :startDate AND :endDate")
    List<ServiceOrder> findOrdersInDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
```

### 2.2 Benefícios do Padrão Repository

1. **Abstração do Acesso a Dados**
   - Isola a lógica de acesso a dados
   - Permite trocar a implementação sem afetar o código cliente
   - Facilita a manutenção

2. **Convenção sobre Configuração**
   - Queries geradas automaticamente baseadas em nomes de métodos
   - Reduz código boilerplate
   - Aumenta produtividade

3. **Tipo Seguro**
   - Erros de compilação em vez de erros em runtime
   - Refatoração mais segura
   - Melhor suporte da IDE

4. **Testabilidade**
   - Facilita mockar o repositório em testes
   - Permite testes de integração usando TestEntityManager
   - Suporte a testes transacionais

### 2.3 Exemplo de Uso em Serviço

```java
@Service
@Transactional
public class ServiceOrderService {
    
    private final ServiceOrderRepository repository;
    
    public ServiceOrderService(ServiceOrderRepository repository) {
        this.repository = repository;
    }
    
    public List<ServiceOrder> findOrdersByCustomer(Long customerId) {
        return repository.findByCustomerId(customerId);
    }
    
    public List<ServiceOrder> findCompletedOrdersByTechnician(Long technicianId) {
        return repository.findByTechnicianIdAndStatus(
            technicianId, 
            ServiceStatus.COMPLETED
        );
    }
}
```

## 3. Component-Based Pattern no React

O Component-Based Pattern é um dos princípios fundamentais do React, onde a interface do usuário é construída através da composição de componentes reutilizáveis, independentes e isolados. Este padrão traz diversos benefícios:

### 3.1 Principais Conceitos

1. **Componentização**
   - Divisão da UI em peças menores e reutilizáveis
   - Cada componente com responsabilidade única
   - Isolamento de lógica e apresentação

2. **Hierarquia de Componentes**
   - Componentes podem conter outros componentes
   - Comunicação via props (top-down)
   - Gerenciamento de estado local ou global

3. **Benefícios**
   - Reusabilidade de código
   - Manutenibilidade
   - Testabilidade
   - Separação de responsabilidades

### 3.2 Exemplo Prático: Criação de Ordem de Serviço

Abaixo, demonstramos como a página de criação de ordem de serviço (`CreateOrderPage`) utiliza componentes reutilizáveis:

```jsx
import { useState } from 'react';
import CustomerDropdown from '../components/CustomerDropdown';
import TechnicianDropdown from '../components/TechnicianDropdown';
import StatusBadge from '../components/StatusBadge';
import { serviceOrderApi } from '../services/apiService';

function CreateOrderPage() {
    // Estados locais para o formulário
    const [formData, setFormData] = useState({
        customerId: '',
        technicianId: '',
        description: '',
        totalCost: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    // Handler para mudanças nos campos
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    // Handler para submit do formulário
    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            setLoading(true);
            setError(null);
            await serviceOrderApi.create(formData);
            // Redirecionar ou mostrar sucesso
        } catch (err) {
            setError('Erro ao criar ordem de serviço');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="page-container">
            <h1 className="text-2xl font-bold mb-6">Nova Ordem de Serviço</h1>
            
            <form onSubmit={handleSubmit} className="bg-white shadow-md rounded-lg p-6">
                {/* Componente reutilizável para seleção de cliente */}
                <CustomerDropdown
                    value={formData.customerId}
                    onChange={handleChange}
                    className="mb-4"
                />

                {/* Componente reutilizável para seleção de mecânico */}
                <TechnicianDropdown
                    value={formData.technicianId}
                    onChange={handleChange}
                    className="mb-4"
                />

                {/* Campo de descrição */}
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Descrição do Serviço
                    </label>
                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        rows={4}
                        className="w-full border rounded-md p-2"
                        required
                    />
                </div>

                {/* Campo de valor */}
                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-1">
                        Valor Total
                    </label>
                    <input
                        type="number"
                        name="totalCost"
                        value={formData.totalCost}
                        onChange={handleChange}
                        className="w-full border rounded-md p-2"
                        required
                    />
                </div>

                {/* Mensagem de erro */}
                {error && (
                    <div className="mb-4 text-red-600">
                        {error}
                    </div>
                )}

                {/* Botões de ação */}
                <div className="flex justify-end space-x-3">
                    <button
                        type="button"
                        className="px-4 py-2 border rounded-md hover:bg-gray-50"
                        onClick={() => history.back()}
                    >
                        Cancelar
                    </button>
                    <button
                        type="submit"
                        disabled={loading}
                        className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-blue-300"
                    >
                        {loading ? 'Salvando...' : 'Salvar'}
                    </button>
                </div>
            </form>
        </div>
    );
}

export default CreateOrderPage;
```

### 3.3 Benefícios Demonstrados

1. **Reusabilidade**
   - `CustomerDropdown` e `TechnicianDropdown` são componentes reutilizáveis
   - Podem ser usados em diferentes partes da aplicação
   - Mantêm sua própria lógica de estado e UI

2. **Manutenibilidade**
   - Cada componente tem responsabilidade única
   - Alterações em um componente não afetam outros
   - Código organizado e fácil de entender

3. **Composição**
   - Página construída através da composição de componentes menores
   - Hierarquia clara de componentes
   - Fluxo de dados via props

4. **Consistência**
   - UI consistente através do reuso de componentes
   - Comportamento padronizado
   - Experiência do usuário uniforme

### 3.4 Integração dos Padrões

A combinação de Dependency Injection com o padrão Repository e Component-Based cria uma arquitetura:
- Desacoplada
- Testável
- Manutenível
- Escalável

Exemplo de fluxo completo:

```text
React Components -> API Service -> Controller (DI) -> Service (DI) -> Repository -> Banco de Dados
      ↑               ↑               ↑                   ↑              ↑
  Componentes     Axios/Fetch    @RestController      @Service     @Repository
```

Esta estrutura em camadas, combinada com os padrões modernos, permite:
- Separação clara de responsabilidades
- Código mais limpo e organizado
- Facilidade de testes
- Manutenção simplificada
- Evolução do sistema de forma sustentável