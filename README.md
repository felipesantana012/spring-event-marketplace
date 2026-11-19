# Marketplace

Projeto didático desenvolvido para praticar Java e Spring Boot por meio da construção de um marketplace de eventos. O foco está no aprendizado de organização arquitetural, Clean Code e recursos do ecossistema Spring que simplificam o desenvolvimento.

## Objetivos de estudo

- Separar domínio, aplicação, infraestrutura e persistência.
- Praticar uma arquitetura inspirada em Clean Architecture/Ports and Adapters.
- Trabalhar com múltiplos bancos de dados no mesmo projeto.
- Utilizar Spring Data JPA, Spring Data MongoDB e Spring Data REST.
- Experimentar processamento assíncrono com `@Async`, `CompletableFuture` e threads virtuais.
- Aplicar validação, auditoria, eventos de persistência e projeções.

## Funcionalidades atuais

- Cadastro de eventos.
- Cadastro de metadados dos eventos, incluindo setores, preços e assentos.
- Cadastro e consulta de clientes.
- Endpoint de showcase que combina eventos do MySQL com seus metadados do MongoDB.
- Enriquecimento assíncrono dos eventos antes da resposta do showcase.
- Logs para acompanhar eventos de persistência e processamento.
- Actuator para endpoints de monitoramento e saúde da aplicação.

## Arquitetura resumida

O projeto está organizado por contexto:

```text
src/main/java/com/example/marketplace
├── catalog
│   ├── application       # casos de uso, enriquecimento e DTOs
│   ├── domain            # entidades e contratos de repositório
│   └── infrastructure    # HTTP, JPA, MongoDB e eventos
└── registration
    ├── domain            # cliente e contrato de repositório
    └── infrastructure    # configuração, persistência e projeções
```

O catálogo utiliza dois tipos de persistência:

- **MySQL:** eventos do catálogo.
- **MongoDB:** metadados dos eventos.

O contexto de registration utiliza outro banco MySQL, separado do banco do catálogo. As configurações de `DataSource`, `EntityManagerFactory` e transações são definidas individualmente para cada contexto.

## Principais endpoints

Os endpoints de eventos, metadados e clientes são expostos pelo Spring Data REST. O endpoint implementado manualmente para consultar o showcase é:

```text
GET http://localhost:8080/showcase
```

Exemplos de endpoints expostos:

```text
POST http://localhost:8080/events
POST http://localhost:8080/eventMetadatas
GET  http://localhost:8080/events
GET  http://localhost:8080/eventMetadatas
GET  http://localhost:8080/customers
```

Para criar metadados, o campo `eventId` deve ser o mesmo UUID de um evento existente no banco de catálogo.

## Tecnologias

- Java 25
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Spring Data MongoDB
- Spring Data REST
- Spring Boot Actuator
- MySQL
- MongoDB
- Gradle
- Docker Compose
- Lombok

## Como executar e testar

### Pré-requisitos

- JDK 25
- Docker e Docker Compose
- Git
- Cliente HTTP, como IntelliJ HTTP Client, Postman ou `curl`

### 1. Baixar o projeto

Clone o repositório e entre na pasta do projeto:

```bash
git clone <URL_DO_REPOSITORIO>
cd marketplace
```

No Windows PowerShell:

```powershell
git clone <URL_DO_REPOSITORIO>
Set-Location marketplace
```

### 2. Subir os bancos de dados

Na raiz do projeto, execute:

```bash
docker compose up -d
```

Confira se os containers estão em execução:

```bash
docker compose ps
```

### 3. Executar a aplicação

Linux/macOS:

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

Ao iniciar, a aplicação ficará disponível em `http://localhost:8080`.

Os serviços definidos no `compose.yaml` utilizam:

| Serviço | Porta |
|---|---:|
| MySQL registration | 3307 |
| MySQL catalog | 3308 |
| MongoDB metadata | 27018 |

O Spring Boot também está configurado para gerenciar o ciclo de vida do Docker Compose durante o desenvolvimento.

### 4. Criar um evento

Crie um evento usando o Spring Data REST:

```http
POST http://localhost:8080/events
Content-Type: application/json

{
  "date": "2026-12-10T20:00:00Z",
  "title": "Evento de teste"
}
```

Copie o UUID retornado no campo `id`. Ele será usado para relacionar os metadados ao evento.

Também é possível usar o arquivo `POST-Events.http` no IntelliJ HTTP Client.

### 5. Criar os metadados do evento

Substitua `<EVENT_ID>` pelo UUID copiado na etapa anterior:

```http
POST http://localhost:8080/eventMetadatas
Content-Type: application/json

{
  "eventId": "<EVENT_ID>",
  "eventDescription": "Evento criado para validar o showcase",
  "sectors": [
    {
      "name": "vip",
      "price": 90.0
    },
    {
      "name": "pista",
      "price": 50.0
    }
  ],
  "seats": [
    {
      "code": "A1",
      "sectorName": "vip"
    },
    {
      "code": "A2",
      "sectorName": "vip"
    },
    {
      "code": "B1",
      "sectorName": "pista"
    }
  ]
}
```

O `eventId` precisa ser exatamente o mesmo UUID de um evento existente no MySQL de catálogo.

### 6. Consultar e validar o showcase

Consulte os eventos enriquecidos:

```http
GET http://localhost:8080/showcase
Accept: application/json
```

A resposta deve conter o evento criado e seus metadados. O enriquecimento é realizado de forma assíncrona antes da resposta.

Também é possível verificar a saúde da aplicação:

```http
GET http://localhost:8080/actuator/health
```

### 7. Executar os testes automatizados

Linux/macOS:

```bash
./gradlew test
```

Windows PowerShell:

```powershell
.\gradlew.bat test
```

### 8. Parar os bancos

Quando terminar os testes:

```bash
docker compose down
```

Para remover também os dados persistidos nos volumes, use somente se quiser reiniciar o ambiente:

```bash
docker compose down -v
```

## Observações

O projeto está em evolução e tem finalidade principalmente educacional. Algumas partes são experimentais, especialmente a composição de dados entre bancos, o processamento assíncrono e a configuração de múltiplos contextos JPA. A propriedade `hibernate.hbm2ddl.auto` está configurada como `update` para preservar os dados durante os reinícios em desenvolvimento.
