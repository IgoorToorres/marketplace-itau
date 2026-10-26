# Customer JPA Repository Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implementar a conversão entre Customer de domínio e entidade JPA e completar todas as operações do adapter de persistência.

**Architecture:** Um mapper sem estado isola a tradução entre os dois modelos. O adapter implementa o contrato de domínio delegando armazenamento ao `CustomerEntityRepository` e usando o mapper em todas as fronteiras.

**Tech Stack:** Java 25, Spring Boot 4.1.2, Spring Data JPA, JUnit 6, AssertJ, MySQL 9 via Docker Compose.

**Spec:** `docs/superpowers/specs/2026-10-26-customer-jpa-repository-design.md`

## Global Constraints

- Separar o nome do domínio no primeiro espaço e preservar todo o restante em `lastName`.
- Concatenar nome e sobrenome somente quando `lastName` tiver texto.
- Preservar `CustomerId` como `UUID` nos dois sentidos.
- Retornar `null` em `findById` quando o registro não existir.
- Não alterar regras de domínio nem adicionar endpoints.

## Review Focus

- Nome com apenas uma palavra deve produzir `lastName=null` e voltar sem espaço extra.
- Nome com sobrenome composto deve preservar todo o texto após o primeiro espaço.
- Sobrenome nulo, vazio ou composto apenas por espaços deve voltar como apenas o primeiro nome.
- Identificador e e-mail devem permanecer iguais após ida e volta pelo mapper.
- Consulta inexistente deve retornar `null`, sem lançar exceção.

---

### Task 1: CustomerMapper

**Files:**
- Create: `src/main/java/dio/marketplace/registration/infrastructure/persistence/mapper/CustomerMapper.java`
- Test: `src/test/java/dio/marketplace/registration/infrastructure/persistence/mapper/CustomerMapperTest.java`

**Interfaces:**
- Consumes: `dio.marketplace.registration.domain.Customer` e `dio.marketplace.registration.infrastructure.persistence.entity.Customer`.
- Produces: `CustomerMapper.toEntity(domain.Customer)` e `CustomerMapper.toDomain(entity.Customer)`.

- [ ] **Step 1: Escrever testes falhando para nome simples, sobrenome composto, sobrenome ausente/em branco e preservação de ID/e-mail.**

- [ ] **Step 2: Executar `./gradlew test --tests '*CustomerMapperTest'` e confirmar falha pela ausência do mapper.**

- [ ] **Step 3: Implementar `CustomerMapper` com separação no primeiro espaço e concatenação condicional.**

- [ ] **Step 4: Reexecutar `./gradlew test --tests '*CustomerMapperTest'` e confirmar sucesso.**

- [ ] **Step 5: Commit opcional, somente se solicitado pelo usuário, com `feat: adiciona mapeamento de clientes para persistência`.**

### Task 2: JpaCustomerRepository

**Files:**
- Modify: `src/main/java/dio/marketplace/registration/infrastructure/persistence/repository/JpaCustomerRepository.java`
- Test: `src/test/java/dio/marketplace/registration/infrastructure/persistence/repository/JpaCustomerRepositoryTest.java`

**Interfaces:**
- Consumes: `CustomerMapper.toEntity(Customer)` e `CustomerMapper.toDomain(entity.Customer)`.
- Produces: implementações funcionais de `save`, `findById`, `findAll` e `delete`.

- [ ] **Step 1: Escrever testes de integração falhando para salvar/consultar, listar, excluir e consultar ID inexistente.**

- [ ] **Step 2: Executar `./gradlew test --tests '*JpaCustomerRepositoryTest'` e confirmar falhas nos métodos incompletos.**

- [ ] **Step 3: Injetar `CustomerMapper` e implementar cada operação delegando ao Spring Data.**

- [ ] **Step 4: Executar os testes do adapter e depois `./gradlew test --rerun-tasks`.**

- [ ] **Step 5: Iniciar com `./gradlew bootRun`, verificar `/actuator/health` e confirmar o container MySQL saudável.**

- [ ] **Step 6: Commit opcional, somente se solicitado pelo usuário, com `feat: implementa repositório JPA de clientes`.**
