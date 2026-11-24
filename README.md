# Marketplace de eventos

Projeto didático em Java e Spring Boot que reúne cadastro de clientes, catálogo de eventos e seleção de assentos. Os módulos usam bancos de dados separados e comunicam mudanças por eventos internos da aplicação.

## Requisitos

- JDK 25
- Docker com Docker Compose
- Git
- Um cliente HTTP, como Postman, IntelliJ HTTP Client ou `curl`

Não é necessário instalar Gradle: o projeto inclui o Gradle Wrapper.

## Preparar e iniciar

Clone o projeto e entre na pasta:

```bash
git clone <URL_DO_REPOSITORIO>
cd marketplace
```

Inicie os bancos e caches definidos em `compose.yaml`:

```bash
docker compose up -d
docker compose ps
```

Espere os serviços ficarem saudáveis antes de iniciar a aplicação. O Compose automático do Spring está desativado neste projeto; por isso, inicie os containers manualmente.

| Serviço | Porta local | Uso |
|---|---:|---|
| MySQL de clientes (registration) | 3307 | Cadastro de clientes |
| MySQL do catálogo | 3308 | Eventos |
| MongoDB | 27018 | Metadados, setores e assentos |
| Redis do catálogo | 6380 | Cache do catálogo |
| PostgreSQL de ticketing | 5433 | Eventos e clientes sincronizados; dados de ticketing |
| Redis de ticketing | 6381 | Bloqueios temporários de assentos |

As credenciais locais definidas no `compose.yaml` são `app`/`app` para MySQL e PostgreSQL. MongoDB e Redis usam a configuração local sem autenticação. Se alguma dessas portas já estiver ocupada, altere o mapeamento em `compose.yaml` e a porta correspondente em `src/main/resources/application.properties`.

Inicie a aplicação em outro terminal, na raiz do projeto:

**Windows PowerShell**

```powershell
.\gradlew.bat bootRun
```

**Linux/macOS**

```bash
./gradlew bootRun
```

A API ficará disponível em `http://localhost:8080`. Verifique a inicialização em:

```text
GET http://localhost:8080/actuator/health
```

## Testar o fluxo de ponta a ponta

Os exemplos abaixo podem ser enviados no Postman ou IntelliJ HTTP Client. Também funcionam com `curl` (no Windows, use `curl.exe`). Os repositórios REST retornam respostas HAL; use o `id` retornado na resposta de cada criação.

### 1. Criar um evento

```http
POST http://localhost:8080/events
Content-Type: application/json

{
  "title": "Show de teste",
  "date": "2026-12-10T20:00:00Z"
}
```

Guarde o `id` do evento para a próxima etapa.

### 2. Cadastrar os metadados, setores e assentos

Substitua `<EVENT_ID>` pelo ID do evento criado. O nome de cada setor em `sectorName` deve corresponder a um `name` em `sectors`.

```http
POST http://localhost:8080/eventMetadatas
Content-Type: application/json

{
  "eventId": "<EVENT_ID>",
  "eventDescription": "Evento de demonstração",
  "sectors": [
    {
      "name": "vip",
      "price": 90.00
    },
    {
      "name": "pista",
      "price": 50.00
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

Ao salvar os metadados, a aplicação publica um evento interno e o módulo de ticketing sincroniza os setores e assentos de forma assíncrona. Aguarde alguns segundos antes de tentar selecionar um assento.

### 3. Cadastrar um cliente

```http
POST http://localhost:8080/customers
Content-Type: application/json

{
  "firstName": "Ana",
  "lastName": "Silva",
  "email": "ana.silva@example.com"
}
```

Guarde o `id` do cliente retornado; ele será enviado no cabeçalho da próxima requisição.

### 4. Selecionar um assento

Substitua `<EVENT_ID>` pelo ID usado nos metadados e `<CUSTOMER_ID>` pelo ID do cliente. O valor de `id` no corpo é o código do assento (`code`) cadastrado nos metadados.

```http
POST http://localhost:8080/ticketing/events/<EVENT_ID>/seats/select
Content-Type: application/json
X-CUSTOMER-ID: <CUSTOMER_ID>

{
  "id": "A1"
}
```

Uma seleção aceita retorna `201 Created`. Um assento inexistente retorna `404 Not Found`; um assento já bloqueado retorna `423 Locked`. O bloqueio temporário expira após aproximadamente 30 segundos.

### 5. Consultar o catálogo

```http
GET http://localhost:8080/showcase
Accept: application/json
```

Outros endpoints REST disponíveis incluem `GET /events`, `GET /eventMetadatas` e `GET /customers`.

## Testes automatizados

Com os serviços do Compose em execução, rode:

**Windows PowerShell**

```powershell
.\gradlew.bat test
```

**Linux/macOS**

```bash
./gradlew test
```

## Encerrar o ambiente

Para parar os containers sem apagar os dados persistidos:

```bash
docker compose down
```

Para apagar também os volumes e todos os dados locais desses bancos, use:

```bash
docker compose down -v
```

> **Atenção:** `docker compose down -v` remove os dados armazenados nos volumes do projeto.

## Estrutura e tecnologias

- **Java 25**, **Spring Boot 4** e **Gradle**
- **Registration:** clientes em MySQL
- **Catalog:** eventos em MySQL e metadados em MongoDB
- **Ticketing:** dados de clientes, eventos, setores e assentos em PostgreSQL; bloqueios temporários em Redis
- **Spring Data REST** para recursos de cadastro e **Spring Web** para showcase e seleção de assentos
- Eventos internos assíncronos para sincronizar clientes e metadados com ticketing
