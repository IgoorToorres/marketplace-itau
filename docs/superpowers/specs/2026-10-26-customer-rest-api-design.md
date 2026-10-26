# API REST de clientes — desenho

## Intenção

Concluir e estabilizar a API de clientes iniciada com Spring Data REST, corrigindo os erros de compilação e de integração presentes nas alterações atuais. A entrega também deve permitir validação automatizada e testes manuais por uma coleção importável no Postman.

## Objetivos

- Manter a exposição automática do repositório pela rota `/customers`.
- Corrigir a entidade `Address`, seu relacionamento com `Customer` e a projeção `excerpt`.
- Fazer o handler receber eventos da entidade JPA e registrar os eventos com SLF4J.
- Preservar a busca por início do primeiro nome, sem diferenciar maiúsculas e minúsculas.
- Manter a exclusão HTTP desabilitada, sem impedir exclusões internas pelo repositório de domínio.
- Cobrir as rotas REST e os comportamentos corrigidos com testes automatizados.
- Entregar uma coleção Postman v2.1 com variáveis `baseUrl` e `customerId`.

## Não objetivos

- Criar controllers ou DTOs REST explícitos.
- Adicionar autenticação, autorização ou regras de negócio novas.
- Levar o endereço para o modelo de domínio nesta etapa.
- Alterar o contrato existente de `CustomerRepository`.

## Abordagens consideradas

1. **Spring Data REST com correções e testes (escolhida):** aproveita as dependências e anotações já adicionadas, mantém o escopo pequeno e fornece HAL, paginação, projeções e busca automaticamente.
2. **Correções mínimas preservando `Adress`:** reduz alterações, mas perpetua um erro de nomenclatura na API e no banco.
3. **Controller e DTOs explícitos:** oferece maior controle sobre o contrato HTTP, mas duplica trabalho que o projeto já decidiu delegar ao Spring Data REST.

## Desenho técnico

- Renomear `Adress` para `Address` e a propriedade `adress` para `address` antes que o novo modelo seja consolidado.
- Aplicar a projeção `CustomerExcerpt` à entidade JPA e usar `org.springframework.beans.factory.annotation.Value` para representar o endereço como texto.
- Fazer `CustomerEventHandler` receber a entidade JPA e usar `org.slf4j.Logger` sem conversões incompatíveis.
- Expor a busca em `/customers/search/findByFirstNameStartingWithIgnoreCase?firstName={valor}`.
- Cobrir `POST`, listagem, consulta individual, busca, atualização, projeção e tentativa de `DELETE`; a tentativa de exclusão deve falhar e preservar o registro.
- Manter `/actuator/health` na coleção Postman para verificação da aplicação.
- Capturar automaticamente o identificador retornado pela criação na variável `customerId` da coleção.

## Organização dos commits

1. Dependências do Spring Data REST.
2. Correções e conclusão da API REST de clientes.
3. Testes de integração da API REST.
4. Documentação técnica e coleção Postman.

## Critérios de conclusão

- O projeto compila e toda a suíte Gradle passa usando o MySQL iniciado pelo Spring Docker Compose.
- O contexto Spring inicia sem falha no handler de eventos.
- As rotas documentadas respondem conforme os testes.
- O arquivo Postman é JSON válido e pode ser importado diretamente.
- Todas as alterações ficam separadas em commits semânticos em português do Brasil.
