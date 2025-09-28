# Drone API - Sistema de Gerenciamento de Drones

Uma API REST para gerenciamento de drones e alocação de pedidos, desenvolvida em Spring Boot com Java 17+.

## 📋 Funcionalidades

- **Gerenciamento de Drones**: Criação, consulta e monitoramento de drones
- **Gerenciamento de Pedidos**: Criação e consulta de pedidos de entrega
- **Alocação Inteligente**: Sistema automático de alocação de pedidos para drones disponíveis
- **Simulação de Voo**: Simulação de voos dos drones com pedidos
- **Validação Robusta**: Validação de capacidade, distância e bateria dos drones
- **Monitoramento**: Sistema de monitoramento em tempo real

## 🚀 Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot 3.5.6**
- **Spring Web MVC**
- **Spring Validation**
- **MapStruct** (Mapeamento de DTOs)
- **JUnit 5** (Testes unitários)
- **Mockito** (Mocking para testes)
- **Maven** (Gerenciamento de dependências)

## 📁 Estrutura do Projeto

```
src/
├── main/java/com/examble/drone_api/
│   ├── config/           # Configurações da aplicação
│   ├── controller/       # Controladores REST
│   ├── dto/             # Data Transfer Objects
│   ├── exception/       # Tratamento de exceções
│   ├── mapper/          # Mapeadores MapStruct
│   ├── model/           # Entidades do domínio
│   ├── repository/      # Repositórios de dados
│   ├── service/         # Lógica de negócio
│   └── validation/      # Validações customizadas
└── test/java/           # Testes unitários
```

## 🛠️ Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- IDE de sua preferência (IntelliJ IDEA, Eclipse, VS Code)

## 📦 Instalação e Execução

### 1. Clone o repositório
```bash
git clone <url-do-repositorio>
cd drone-api
```

### 2. Compile o projeto
```bash
mvn clean compile
```

### 3. Execute os testes
```bash
mvn test
```

### 4. Execute a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 🔧 Configuração

### Porta da Aplicação
A aplicação roda na porta 8080 por padrão. Para alterar, edite o arquivo `application.properties`:

```properties
server.port=8081
```

### Configurações de Validação
As validações estão configuradas nos DTOs com anotações Bean Validation:

- **Drone**: Limite de peso (50 kg), distância (1-100 km), posição (1-100)
- **Pedido**: Coordenadas (1-100), peso (1-1000 kg)

## 📚 API Endpoints

### Drones

#### Criar Drone
```http
POST /api/v1/drones
Content-Type: application/json

{
  "weightLimit": 50,
  "distancePerCargo": 20,
  "positionX": 1,
  "positionY": 1
}
```

#### Listar Todos os Drones
```http
GET /api/v1/drones
```

#### Buscar Drone por ID
```http
GET /api/v1/drones/{id}
```

#### Iniciar Voo dos Drones
```http
POST /api/v1/drones/fly
```

### Pedidos

#### Criar Pedido
```http
POST /api/v1/pedidos
Content-Type: application/json

{
  "destinationX": 10,
  "destinationY": 15,
  "weight": 25,
  "priority": "HIGH"
}
```

#### Listar Todos os Pedidos
```http
GET /api/v1/pedidos
```

#### Buscar Pedido por ID
```http
GET /api/v1/pedidos/{id}
```

## 🧪 Testes

O projeto possui uma cobertura abrangente de testes unitários:

### Executar Todos os Testes
```bash
mvn test
```

### Executar Testes com Relatório de Cobertura
```bash
mvn test jacoco:report
```

### Estrutura dos Testes
- **Model Tests**: Testes das entidades Drone e Order
- **Service Tests**: Testes da lógica de negócio
- **Controller Tests**: Testes dos endpoints REST
- **Validation Tests**: Testes das validações customizadas

## 📊 Monitoramento

### Status dos Drones
- **IDLE**: Drone disponível para pedidos
- **IN_FLIGHT**: Drone em voo
- **CHARGING**: Drone carregando bateria

### Critérios de Alocação
- Capacidade de peso disponível
- Distância máxima de voo
- Nível de bateria (mínimo 20%)
- Prioridade do pedido
- Posição atual do drone

## 🔍 Validações

### Drone
- Limite de peso: 50 kg
- Distância por carga: 1-100 km
- Posição inicial: 1-100 (X e Y)
- Bateria mínima para voo: 20%

### Pedido
- Coordenadas de destino: 1-100
- Peso: 1-1000 kg
- Prioridade: LOW, MEDIUM, HIGH

## 🚨 Tratamento de Erros

A API possui tratamento robusto de erros com códigos HTTP apropriados:

- **400 Bad Request**: Dados inválidos ou validação falhou
- **404 Not Found**: Recurso não encontrado
- **409 Conflict**: Conflito na alocação de pedidos
- **500 Internal Server Error**: Erro interno do servidor

## 📈 Exemplos de Uso

### 1. Criar um Drone
```bash
curl -X POST http://localhost:8080/api/v1/drones \
  -H "Content-Type: application/json" \
  -d '{
    "weightLimit": 50,
    "distancePerCargo": 20,
    "positionX": 1,
    "positionY": 1
  }'
```

### 2. Criar um Pedido
```bash
curl -X POST http://localhost:8080/api/v1/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "destinationX": 10,
    "destinationY": 15,
    "weight": 25,
    "priority": "HIGH"
  }'
```

### 3. Iniciar Voo dos Drones
```bash
curl -X POST http://localhost:8080/api/v1/drones/fly
```

## 🛡️ Segurança

- Validação de entrada em todos os endpoints
- Tratamento de exceções centralizado
- Logs estruturados para auditoria

## 📝 Logs

A aplicação gera logs estruturados para:
- Criação de drones e pedidos
- Alocação de pedidos
- Simulação de voos
- Erros e exceções

