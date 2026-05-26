# java-api

## Sobre o projeto

Eu criei essa API em Java para praticar arquitetura em camadas, regras de negócio reais, persistencia com Hibernate/JPA, PostgreSQL, Swagger e testes unitarios.

Comecei com uma base simples e fui evoluindo ate chegar em um CRUD completo de funcionarios com autorrelacionamento (gestor e subordinados), regras de salario liquido e consultas de analytics.

---

## Stack que eu usei

- Java 21
- Spring Boot 4.0.6
- Spring Web
- Spring Data JPA + Hibernate
- PostgreSQL
- springdoc OpenAPI (Swagger)
- JUnit 5

---

## O que eu implementei

### 1. API base e estrutura

Eu organizei o projeto em camadas:

- controller
- application (services e DTOs)
- domain (entidades e enums)
- infrastructure (repository)

Tambem ajustei o debug do VS Code para rodar direto com F5 usando a classe principal correta no pacote raiz da aplicacao.

### 2. Persistencia com Hibernate/JPA

Eu configurei JPA/Hibernate no projeto e modelei a entidade Employee para banco relacional com:

- id identity
- nome
- data de nascimento
- tipo de contrato
- cargo
- salario base
- autorrelacionamento de gestor/subordinados

No banco, o enum esta salvo como texto (STRING), nao ordinal. Exemplo de valores: Administrative, Technician, Manager.

### 3. PostgreSQL local

Eu configurei a API para conectar no PostgreSQL local e resolvi os problemas de permissao de schema para conseguir criar e manipular objetos normalmente.

### 4. CRUD completo de employees

Eu implementei controller + application service + repository com endpoints para:

- listar funcionarios
- buscar por id
- criar
- atualizar
- deletar
- retornar salario liquido por funcionario

### 5. Regras de negocio de salario

Eu implementei as regras:

- CLT: salario liquido com desconto de 30%
- PJ: salario liquido com desconto de 10%
- Gestor: salario liquido com desconto do contrato + bonus de 100 por subordinado

### 6. Performance na listagem

Depois que eu populei o banco com milhares de registros, ajustei a listagem para escalar melhor:

- paginacao no banco (em vez de paginar em memoria)
- consulta dedicada para vinculos gestor -> subordinado
- reducao de N+1 para montar subordinateIds no response

### 7. Swagger / OpenAPI

Eu configurei a documentacao da API com springdoc:

- OpenAPI JSON: /api-docs
- Swagger UI: /swagger

### 8. Testes unitarios

Eu escrevi testes cobrindo os requisitos de negocio (RQ01 ate RQ09), incluindo:

- dados obrigatorios da entidade
- cargos suportados
- alteracao de salario base
- calculo de salario liquido para CLT, PJ e gestor
- filtros e transformacoes de analytics
- soma de idades

Resultado: todos os testes passam.

---

## Requisitos que eu cobri

- RQ01: Funcionario deve ter nome, data de nascimento, tipo de contrato e salario base
- RQ02: Funcionario pode ser administrativo, tecnico ou gestor
- RQ03: Funcionario pode ter salario base alterado
- RQ04: Salario liquido CLT = salario base - 30%
- RQ05: Salario liquido PJ = salario base - 10%
- RQ06: Salario liquido do gestor = salario com desconto + 100 por subordinado
- RQ07: Listar apenas funcionarios com salario liquido menor que 3000
- RQ08: Retornar apenas o primeiro nome dos funcionarios
- RQ09: Somar idade em anos de todos os funcionarios

---

## Como eu rodo o projeto

Na pasta api:

1. rodar testes
2. subir aplicacao

Comandos:

```bash
./mvnw test
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

---
