# Customer REST API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Corrigir e concluir a API Spring Data REST de clientes, comprová-la com testes de integração e entregar uma coleção Postman importável.

**Architecture:** O `CustomerEntityRepository` continua sendo a fronteira HTTP gerenciada pelo Spring Data REST. A entidade JPA agrega `Address`, a projeção `CustomerExcerpt` fornece a representação resumida e o handler observa eventos do repositório; testes com MockMvc exercitam o contrato HTTP real contra o MySQL iniciado pelo Spring Docker Compose.

**Tech Stack:** Java 25, Spring Boot 4.1.2, Spring Data JPA, Spring Data REST, Jakarta Validation, MySQL 9, JUnit 5, MockMvc, Gradle e Postman Collection v2.1.

**Spec:** `docs/superpowers/specs/2026-10-26-customer-rest-api-design.md`

## Global Constraints

- Manter a API automática do Spring Data REST em `/customers`.
- Manter HTTP `DELETE` não exportado, preservando a exclusão interna pelo repositório Java.
- Usar o nome correto `Address` no código, no relacionamento e no JSON.
- Não criar controller, DTO REST, autenticação ou regras de domínio novas.
- Preservar todas as alterações locais existentes e gerar commits semânticos em português do Brasil.

## Review Focus

- Uma criação sem `id` deve gerar UUID e devolver `Location`; cobrir no teste de criação.
- O endereço aninhado deve persistir e aparecer como texto na projeção da coleção; cobrir no teste de projeção.
- A busca deve ignorar maiúsculas e minúsculas e filtrar pelo início do primeiro nome; cobrir no teste de busca.
- Uma tentativa de `DELETE` HTTP deve falhar sem remover o cliente; cobrir no teste de método bloqueado.
- O handler deve iniciar e processar a entidade JPA sem conversão incompatível de logger; cobrir pelo carregamento do contexto e pelas operações REST.

---

### Task 1: Dependências do Spring Data REST

**Files:**
- Modify: `build.gradle`

**Interfaces:**
- Consumes: configuração Gradle existente do Spring Boot.
- Produces: Spring Data REST e HAL Explorer disponíveis no classpath.

- [ ] **Step 1: Confirmar as dependências adicionadas**

Verificar que `spring-boot-starter-data-rest` e `spring-data-rest-hal-explorer` estão declarados sem versões explícitas, usando o gerenciamento do Spring Boot.

- [ ] **Step 2: Verificar a resolução**

Run: `./gradlew dependencies --configuration runtimeClasspath`
Expected: exit code 0 e ambas as dependências resolvidas.

- [ ] **Step 3: Reservar o commit semântico**

Arquivos deste commit: somente `build.gradle`.

Mensagem: `build: adiciona suporte ao Spring Data REST`

### Task 2: Contrato REST e testes de regressão

**Files:**
- Create: `src/test/java/dio/marketplace/registration/infrastructure/rest/CustomerRestApiTest.java`
- Modify: `src/test/java/dio/marketplace/MarketplaceApplicationTests.java` apenas se necessário para uma propriedade de teste compartilhada.

**Interfaces:**
- Consumes: `/customers`, a busca `findByFirstNameStartingWithIgnoreCase` e o MySQL do Compose.
- Produces: testes executáveis que fixam criação, projeção, consulta, busca, atualização e bloqueio de exclusão.

- [ ] **Step 1: Escrever o teste de criação e consulta**

Criar cliente via `POST /customers` com `firstName`, `lastName`, `email` e `address`; afirmar status `201`, cabeçalho `Location`, UUID gerado e consulta individual com os dados persistidos.

- [ ] **Step 2: Escrever o teste da projeção da coleção**

Consultar `GET /customers`; afirmar que o item expõe `firstName`, `lastName` e `address` no formato `rua, CEP, cidade, estado` pela projeção `excerpt`.

- [ ] **Step 3: Escrever os testes de busca e atualização**

Buscar com `firstName` em caixa diferente e afirmar somente nomes com o prefixo; atualizar via `PATCH /customers/{id}` e afirmar o novo sobrenome.

- [ ] **Step 4: Escrever o teste de exclusão bloqueada**

Executar `DELETE /customers/{id}`; afirmar `405 Method Not Allowed` e confirmar que o registro continua existente.

- [ ] **Step 5: Executar para comprovar o estado vermelho**

Run: `./gradlew test --tests '*CustomerRestApiTest' --rerun-tasks`
Expected: FAIL no estado atual por erros de compilação e, após a primeira correção isolada, por violações do contrato REST ainda não implementado.

### Task 3: Corrigir e concluir a infraestrutura REST

**Files:**
- Rename: `src/main/java/dio/marketplace/registration/infrastructure/persistence/entity/Adress.java` para `src/main/java/dio/marketplace/registration/infrastructure/persistence/entity/Address.java`
- Modify: `src/main/java/dio/marketplace/registration/infrastructure/persistence/entity/Customer.java`
- Modify: `src/main/java/dio/marketplace/registration/infrastructure/persistence/entity/projection/CustomerExcerpt.java`
- Modify: `src/main/java/dio/marketplace/registration/infrastructure/persistence/repository/CustomerEntityRepository.java`
- Modify: `src/main/java/dio/marketplace/registration/infrastructure/event/CustomerEventHandler.java`

**Interfaces:**
- Consumes: entidades enviadas pelo Spring Data REST e operações do `CustomerEntityRepository`.
- Produces: propriedade `Customer.address`, projeção `CustomerExcerpt`, busca por prefixo e eventos após create/save/delete.

- [ ] **Step 1: Corrigir `Address` e o agregado JPA**

Renomear classe e arquivo; manter UUID gerado, campos `street`, `postalCode`, `city`, `state`, `createdOn` e a representação textual. Alterar `Customer` para `private Address address` com cascata e remoção de órfão.

- [ ] **Step 2: Corrigir a projeção**

Aplicar `@Projection` à entidade JPA e importar `org.springframework.beans.factory.annotation.Value`; mapear `#{target.address?.toString()}` para `getAddress()`.

- [ ] **Step 3: Corrigir o handler**

Restringir `@RepositoryEventHandler` à entidade JPA, receber esse tipo nos três métodos e usar `org.slf4j.Logger` com `LoggerFactory` sem cast.

- [ ] **Step 4: Bloquear completamente o DELETE HTTP**

Manter `deleteById(UUID)` e também sobrescrever `delete(Customer)` com `@RestResource(exported = false)`, garantindo que operações Java continuem disponíveis.

- [ ] **Step 5: Executar o teste REST até ficar verde**

Run: `./gradlew test --tests '*CustomerRestApiTest' --rerun-tasks`
Expected: PASS.

- [ ] **Step 6: Executar toda a suíte**

Run: `./gradlew test --rerun-tasks`
Expected: `BUILD SUCCESSFUL` sem falhas.

- [ ] **Step 7: Reservar os commits semânticos**

Commit da implementação: `fix: corrige infraestrutura REST de clientes`

Commit dos testes: `test: cobre API REST de clientes`

### Task 4: Coleção Postman e documentação

**Files:**
- Create: `postman/marketplace.postman_collection.json`
- Modify: `docs/superpowers/specs/2026-10-26-customer-rest-api-design.md` somente se a implementação revelar alguma decisão diferente.
- Create: `docs/superpowers/plans/2026-10-26-customer-rest-api.md`

**Interfaces:**
- Consumes: contrato HTTP validado pelos testes REST.
- Produces: coleção Postman v2.1 com `baseUrl`, `customerId` e `customerEmail`.

- [ ] **Step 1: Criar a coleção Postman**

Adicionar requisições para health check, raiz HAL, criação, listagem, consulta individual, busca, atualização e tentativa de exclusão. Gerar e-mail único antes da criação e capturar o UUID a partir do cabeçalho `Location` em `customerId`.

- [ ] **Step 2: Adicionar asserções manuais automatizadas**

Cada requisição deve validar o status esperado; a criação deve validar `Location`, e a exclusão deve esperar `405`.

- [ ] **Step 3: Validar o JSON**

Run: `python3 -m json.tool postman/marketplace.postman_collection.json`
Expected: exit code 0.

- [ ] **Step 4: Validar diferenças e suíte final**

Run: `git diff --check && ./gradlew test --rerun-tasks`
Expected: nenhum erro no diff e `BUILD SUCCESSFUL`.

- [ ] **Step 5: Criar o commit de documentação**

Mensagem: `docs: adiciona coleção Postman da API de clientes`

Arquivos: coleção Postman, desenho e este plano.

### Task 5: Organizar e auditar os commits

**Files:**
- Review: todos os arquivos alterados nesta entrega.

**Interfaces:**
- Consumes: alterações verificadas das tarefas anteriores.
- Produces: histórico limpo e árvore de trabalho sem alterações pendentes.

- [ ] **Step 1: Remover o stage antigo sem alterar arquivos**

Run: `git reset`
Expected: todas as mudanças permanecem no working tree e o index fica vazio.

- [ ] **Step 2: Criar os quatro commits na ordem planejada**

Criar, em ordem: `build`, `fix`, `test`, `docs`, conferindo `git diff --cached --check` e `git diff --cached --name-status` antes de cada commit.

- [ ] **Step 3: Executar a verificação pós-commit**

Run: `./gradlew test --rerun-tasks && git status --short --branch && git log --oneline -4`
Expected: suíte verde, working tree limpa e quatro commits novos na ordem prevista.
