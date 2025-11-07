# Roteiro da Apresentação - Sistema de Gerenciamento de Oficina Mecânica

## Slide 1: Introdução (1 minuto)
* Apresentação do Problema
  - Oficina mecânica necessitando modernização
  - Gestão manual e propensa a erros
  - Dificuldade no acompanhamento de serviços
  - Comunicação ineficiente com clientes

## Slide 2: Requisitos Principais (1 minuto)
* Necessidades do Negócio
  - Cadastro e gestão de clientes e veículos
  - Controle de ordens de serviço
  - Alocação de técnicos
  - Notificações automáticas
  - Histórico de serviços

## Slide 3: Arquitetura em Camadas (2 minutos)
* Justificativa Técnica
  - Separação clara de responsabilidades
  - Manutenibilidade aprimorada
  - Testabilidade facilitada
  - Escalabilidade independente por camada

* Stack Escolhido
  - Backend: Spring Boot
    * Maturidade e robustez
    * Amplo ecossistema
    * Suporte a padrões de projeto
  - Frontend: React
    * Componentização
    * Performance com Virtual DOM
    * Experiência do usuário moderna

## Slide 4-6: Padrões de Projeto Implementados (3 minutos)

### Slide 4: Builder Pattern
* Onde: Criação de Ordens de Serviço
* Por quê?
  - Construção complexa com muitos atributos
  - Validações durante a construção
  - Interface fluente e legível
* Demonstração de código do ServiceOrderBuilder

### Slide 5: Observer Pattern
* Onde: Notificações de Status
* Por quê?
  - Desacoplamento entre serviço e notificações
  - Extensibilidade para novos tipos de notificação
  - Manutenção simplificada
* Demonstração: ServiceOrderService notificando EmailObserver

### Slide 6: Facade Pattern
* Onde: Coordenação de Serviços
* Por quê?
  - Simplificação da interface para controllers
  - Encapsulamento de operações complexas
  - Redução de acoplamento
* Demonstração: WorkshopFacade orquestrando múltiplos serviços

## Slide 7-8: Padrões de Framework (2 minutos)

### Slide 7: Dependency Injection
* Benefícios
  - Baixo acoplamento
  - Testabilidade
  - Manutenibilidade
* Demonstração
  - Injeção via construtor em Controllers
  - Configuração automática do Spring

### Slide 8: Repository Pattern
* Benefícios
  - Abstração do acesso a dados
  - Queries declarativas
  - Tipo seguro
* Demonstração
  - Interface ServiceOrderRepository
  - Queries customizadas

## Slide 9: Conclusões e Aprendizados (1 minuto)
* Técnicos
  - Importância da arquitetura bem definida
  - Valor dos padrões de projeto
  - Produtividade com frameworks modernos

* Negócio
  - Solução escalável
  - Manutenção facilitada
  - Base sólida para evolução

* Equipe
  - Colaboração efetiva
  - Conhecimento compartilhado
  - Crescimento técnico

## Notas para Apresentação

1. Timing
   - Manter rigorosamente o tempo de cada seção
   - Reservar 30s para perguntas no final
   - Praticar transições entre slides

2. Demonstrações de Código
   - Preparar snippets relevantes
   - Destacar partes importantes
   - Manter exemplos concisos

3. Dicas
   - Começar com o problema de negócio
   - Enfatizar decisões técnicas
   - Mostrar valor agregado
   - Manter linguagem clara e objetiva
   - Usar analogias quando necessário