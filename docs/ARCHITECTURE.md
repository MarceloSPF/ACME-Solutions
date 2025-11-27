# Documento de Arquitetura - Sistema de Gestão de Oficina Mecânica

## 1. Visão Geral da Arquitetura

O sistema foi desenvolvido utilizando uma Arquitetura em Camadas (Layered Architecture) com Spring Boot no backend e React no frontend.

## 2. Justificativa da Arquitetura em Camadas

A Arquitetura em Camadas foi escolhida pelos seguintes motivos:

### 2.1 Separação de Responsabilidades

Cada camada tem uma responsabilidade específica e bem definida. Esta divisão clara permite que cada camada se concentre em uma única tarefa, facilitando a compreensão e o desenvolvimento do sistema. A separação também evita que mudanças em uma camada afetem outras camadas desnecessariamente.

### 2.2 Manutenibilidade

A arquitetura em camadas facilita significativamente a manutenção do código. Como as responsabilidades estão bem definidas, alterações podem ser feitas em uma camada específica sem afetar as outras. Isso torna o sistema mais estável e reduz o risco de regressões durante manutenção.

### 2.3 Testabilidade

Cada camada pode ser testada de forma isolada, permitindo testes unitários e de integração mais eficazes. A separação entre camadas facilita mockar dependências e validar o comportamento específico de cada camada sem precisar envolver todas as outras.

### 2.4 Escalabilidade

A arquitetura possibilita escalar diferentes camadas de forma independente. Por exemplo, a camada de apresentação pode ser distribuída em múltiplos servidores enquanto a camada de persistência permanece em um servidor único, ou vice-versa, conforme as necessidades de desempenho.

### 2.5 Reutilização

Componentes podem ser reutilizados em diferentes partes do sistema. Serviços desenvolvidos para uma funcionalidade podem ser usados por múltiplos controllers, e componentes React podem ser compostos e reutilizados em diferentes páginas.

## 3. Stack Tecnológico

### 3.1 Backend: Spring Boot

Spring Boot foi escolhido pelo seu conjunto completo de características. É o framework Java mais utilizado no mercado com uma longa história de sucesso em produção. Oferece diversos módulos prontos como Spring Data JPA, Spring Security e Spring Actuator que aceleram o desenvolvimento. Fornece excelente desempenho e otimização automática de recursos através da JVM. Possui recursos robustos de segurança integrados que simplificam a implementação de autenticação e autorização. Tem ampla documentação oficial, tutoriais e uma comunidade ativa pronta para ajudar. Com suporte completo para Java 21, oferece todas as melhorias de performance da versão mais recente.

### 3.2 Frontend: React

React foi escolhido por suas características que impactam positivamente o desenvolvimento. Facilita a criação de interfaces reutilizáveis através de componentes isolados. O Virtual DOM oferece otimizações automáticas de performance durante atualizações de interface. Oferece uma grande quantidade de bibliotecas e componentes disponíveis para resolver diversos problemas. O padrão de componentes torna o código mais previsível e fácil de manter. Possui ampla comunidade e suporte empresarial direto do Meta, garantindo evolução contínua.

### 3.3 Banco de Dados: PostgreSQL

PostgreSQL foi escolhido como banco de dados porque é a database open-source mais confiável disponível com excelente reputação em produção. Oferece suporte para tipos de dados complexos, índices avançados e consultas sofisticadas. Fornece excelente performance para leitura e escrita de dados. Suporta replicação e particionamento para sistemas em larga escala. Garante consistência e integridade dos dados através de transações ACID.

## 4. Estrutura do Projeto

O projeto está organizado nas seguintes camadas:

### 4.1 Camada de Apresentação (Frontend)

A camada de apresentação é responsável pela interface com o usuário. Ela contém componentes React reutilizáveis que formam a UI, gerenciamento de estado local ou global para controlar o comportamento da interface, rotas e navegação para permitir navegação entre páginas, e integração com a API do backend através de chamadas HTTP.

### 4.2 Camada de Aplicação (Backend)

A camada de aplicação gerencia as requisições do cliente e orquestra o fluxo de dados. Contém Controllers que expõem endpoints REST para o frontend, Services que encapsulam a lógica de negócio e regras do domínio, e DTOs que definem o contrato de comunicação com o cliente.

### 4.3 Camada de Domínio

A camada de domínio define as entidades de negócio do sistema. Contém as classes de modelo que representam os conceitos principais como Customer, Technician, Vehicle e ServiceOrder. Define interfaces de repositório que estabelecem contratos para acesso a dados. Implementa regras de domínio que encapsulam lógica específica do negócio.

### 4.4 Camada de Persistência

A camada de persistência gerencia a comunicação com o banco de dados. Contém implementações de repositório que utilizam Spring Data JPA, mapeamento ORM que define como as entidades Java são persistidas no banco relacional, e configurações de banco de dados que definem conexão, pool de conexões e outras propriedades.

## 5. Fluxo de Dados

O fluxo de dados na aplicação segue a seguinte sequência: O usuário interage com a interface React no frontend. Componentes React capturam as ações do usuário. Chamadas HTTP são feitas para os endpoints REST do backend. Controllers recebem as requisições e delegam para Services. Services executam a lógica de negócio e delegam para Repositories. Repositories acessam o banco de dados através do ORM. Os dados retornam através das camadas seguindo o fluxo inverso. O frontend atualiza a UI com os dados retornados.

Esta estrutura em camadas garante que cada componente tem uma responsabilidade clara e bem definida, facilitando manutenção, testes e evolução do sistema.

## 6. Padrões Arquiteturais Utilizados

### 6.1 Dependency Injection

Utilizado para desacoplar as classes e permitir injeção de dependências através do container Spring. Permite fácil substituição de implementações e simplifica testes.

### 6.2 Repository Pattern

Utilizado para abstrair o acesso a dados e permitir consultas type-safe usando Spring Data JPA. Oferece métodos prontos para operações CRUD e permite definir consultas customizadas.

### 6.3 DTO Pattern

Utilizado para separar a representação interna das entidades da representação enviada ao cliente. Permite controlar quais dados são expostos e simplifica a evolução da API.

### 6.4 Component-Based Pattern

Utilizado no frontend React para criar componentes reutilizáveis e isolados que podem ser compostos para formar interfaces complexas.

## 7. Benefícios da Arquitetura

A arquitetura implementada proporciona diversos benefícios: Permite escalabilidade através do crescimento independente de cada camada conforme as necessidades. Facilita manutenibilidade através de responsabilidades bem definidas. Permite testabilidade com cada camada testável isoladamente com facilidade. Promove reusabilidade onde componentes e serviços podem ser reutilizados em diferentes contextos. Oferece flexibilidade para trocar implementações com mínimo impacto. Facilita evolução com adição de novas funcionalidades sem afetar código existente.
