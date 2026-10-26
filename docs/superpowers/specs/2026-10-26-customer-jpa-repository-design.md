# Repositório JPA de Customer — Design

## Intenção

Completar o adapter `JpaCustomerRepository` sem acoplar o domínio às classes JPA e manter uma conversão reversível entre o nome completo do domínio e `firstName`/`lastName` da entidade.

## Decisões

- Um `CustomerMapper` dedicado fará as conversões nos dois sentidos.
- Domínio para entidade: separar `name` no primeiro espaço; a primeira parte será `firstName` e todo o restante será `lastName`. Sem espaço, `lastName` será `null`.
- Entidade para domínio: concatenar `firstName` e `lastName` com um espaço quando o sobrenome tiver texto; caso contrário, usar apenas `firstName`.
- `CustomerId.id()` será persistido diretamente como `UUID` e reconstruído ao voltar ao domínio.
- `JpaCustomerRepository` implementará `save`, `findById`, `findAll` e `delete`; `findById` retornará `null` quando não houver registro, preservando o contrato atual.

## Abordagens consideradas

- Separar no primeiro espaço: escolhida porque preserva todo o restante do nome como sobrenome.
- Separar no último espaço: rejeitada porque muda a interpretação de nomes compostos.
- Gravar o nome inteiro em `firstName`: rejeitada porque enfraquece o modelo da entidade.

## Limites

Não serão criados endpoints, casos de uso ou novas regras de validação do domínio. O timestamp `createdOn` permanece responsabilidade do Hibernate e não faz parte do domínio.
