# TCC Java - Minha Rede Social (API)

### Objetivo

O projeto **Minha Rede Social** consiste em desenvolver a API RESTful de uma rede social com algum tema de livre escolha, seguindo os requisitos técnicos e de negócio detalhados a seguir.

### Formato

Trabalho individual.

---

## Requisitos Necessários

### 1. Cadastro de usuário

A API deve expor um recurso para o registro de novos usuários. O payload de entrada deve validar os seguintes dados:

| Dados Necessários  | Tipo      | Regras                                             |
| :----------------- | :-------- | :------------------------------------------------- |
| NOME COMPLETO      | String    | Obrigatório, máximo de 255 caracteres              |
| EMAIL              | String    | Obrigatório, máximo de 255 caracteres              |
| APELIDO            | String    | Opcional, máximo de 50 caracteres                  |
| DATA DE NASCIMENTO | LocalDate | Obrigatório                                        |
| SENHA              | String    | Obrigatório, máximo de 128 caracteres              |
| IMAGEM DE PERFIL   | String    | Opcional, máximo de 512 caracteres (URL ou Base64) |

**Observações:**

- O e-mail deve ser uma restrição única (Unique Constraint) no banco e validado na API; cadastros duplicados devem retornar o erro HTTP adequado.
- A senha deve ser obrigatoriamente criptografada antes da persistência (ex: BCrypt).

### 2. Autenticação

A API deve possuir um endpoint de autenticação que valide credenciais e emita um token de acesso (ex: JWT) ou estabeleça uma sessão segura para controle de autorização nos demais endpoints privados.

### 3. Dashboard / Feed de Publicações

A API deve prover os dados consolidados para a visualização principal do perfil do usuário autenticado:

- Retornar o payload com os dados do próprio usuário logado.
- Retornar a lista de solicitações de amizade pendentes recebidas.
- Disponibilizar endpoint para a criação de novas postagens.
- Disponibilizar endpoint de listagem paginada contendo os posts do próprio usuário e de sua rede de amigos, ordenados cronologicamente (mais recentes primeiro).
  - Para cada post retornado, deve haver a possibilidade de registrar uma curtida e de remover uma curtida existente.
  - Para cada post, deve haver suporte para a inclusão de comentários vinculados.

**Observações:**

- As postagens devem possuir controle de ACL/Visibilidade: `público` (acessível a qualquer usuário autenticado que consulte o perfil) ou `privado` (acessível exclusivamente às conexões de amizade do autor). Deve existir um endpoint para atualizar essa permissão de visualização sob demanda.

### 4. Busca de Contatos

A API deve fornecer um recurso de pesquisa de usuários na base de dados para viabilizar o envio de solicitações de amizade.

**Observações:**

- A pesquisa deve aceitar um **único parâmetro de busca** (query param) que aplique o filtro simultaneamente em `nome` ou `e-mail`.
- A regra de negócio deve bloquear via API o envio de solicitações para o próprio usuário autenticado ou para perfis que já possuam vínculo de amizade ativo.

### 5. Gestão de Amizades

A API deve expor recursos para listar os vínculos de amizade consolidados e permitir o encerramento (exclusão) dessas relações.

**Observações:**

- A listagem também deve suportar filtragem usando um **único parâmetro** que busque por nome ou e-mail.
- Deve ser possível obter os detalhes completos (perfil) de um amigo a partir de seu identificador.

### 6. Perfil de Terceiros

A API deve permitir a consulta de informações de perfis de terceiros. O payload de resposta deve incluir obrigatoriamente os posts configurados como `público`.

**Observações:**

- A camada de serviço deve verificar a existência de vínculo de amizade entre o solicitante e o perfil consultado; caso positivo, os posts `privados` também devem ser agregados à resposta.
- A API deve prover as rotas adequadas para envio de solicitação de amizade ou remoção do vínculo, dependendo do estado atual da relação.

### 7. Edição de Perfil

A API deve disponibilizar um endpoint de atualização (PUT/PATCH) para que o usuário modifique seus próprios dados de perfil: nome, apelido e imagem.

---

## Observações Arquiteturais e de Negócio

- Utilizar os padrões de projeto e arquitetura consolidados do ecossistema Java.
- As regras documentadas não ditam a implementação técnica (como as tabelas devem ser modeladas ou os endpoints nomeados), mas definem os cenários que a API deve suportar obrigatoriamente.
- **Segurança e Validação:** Implementar validações de _input_ em todas as rotas (ex: `javax.validation` / `jakarta.validation`). Lançar exceções coerentes (400 Bad Request, 403 Forbidden, 404 Not Found).
- O tratamento de erros deve ser centralizado e retornar mensagens claras (payload padronizado para erros de validação e exceções não mapeadas).
- Não é obrigatório cobrir fluxos alternativos além do escopo exigido, mas extensões lógicas de domínio são bem-vindas se mantiverem a coesão.

### FAQ Técnico

- **Como funciona a máquina de estados de amizade?**

  O fluxo base de amizade deve seguir uma pequena máquina de estados. Quando o `usuarioA` envia uma solicitação para o `usuarioB`, a relação nasce como `PENDING`. Quando o `usuarioB` aceita a solicitação, a relação passa para `ACCEPTED`, liberando as funcionalidades dependentes de amizade, como acesso a posts privados. Opcionalmente, o projeto pode prever estados adicionais como recusa ou cancelamento, desde que o fluxo principal exigido seja respeitado.

- **O que priorizar?**

  A prioridade máxima é o cumprimento integral dos requisitos funcionais descritos no enunciado. Em caso de conflito entre adicionar complexidade arquitetural extra e entregar os cenários obrigatórios corretamente, a prioridade deve ser sempre a cobertura completa do escopo funcional com validação, segurança, regras de negócio e contratos consistentes.

- **Preciso implementar testes unitários?**

  Sim. O projeto exige cobertura de testes unitários para as camadas de `services`, `validators` e `mappers`. O objetivo dos testes é comprovar que as regras de negócio, validações e transformações entre entidades e DTOs estão corretas, sem depender exclusivamente de testes de controller ou de contexto da aplicação.

- **Posso retornar as Entidades de Domínio/JPA diretamente na Controller?**

  Não. Entidades de domínio ou JPA não devem ser expostas pela controller, porque isso viola o isolamento entre camadas e acopla o contrato externo da API ao modelo interno de persistência. O retorno da API deve ser sempre feito por meio de DTOs de resposta apropriados.

- **Posso retornar a Entidade se ela estiver encapsulada (wrappeada) dentro de um Response/DTO genérico?**

  Também não. Encapsular uma entidade dentro de um wrapper não elimina a violação arquitetural, porque a entidade continua sendo exposta na borda da aplicação. O fluxo correto deve ser mantido da seguinte forma: `Controller[Request, Response] -> Service -> [Mapper, Repository, Service]`. Em outras palavras, a controller recebe DTO de entrada, o service executa a regra de negócio e o mapper transforma entidades em DTOs de saída antes da resposta ser devolvida ao cliente.

## Diretrizes de Execução

1. Desenhe o modelo lógico e relacional do banco de dados antes de iniciar o código.
2. Estruture o projeto da API definindo as camadas adequadamente.
3. Crie os mapeamentos de entidades (ORM) e configurações de banco.
4. Para cada módulo/funcionalidade:
   - Defina os contratos (DTOs).
   - Implemente a lógica no Service.
   - Exponha a Controller e valide as requisições (ex: Postman/Insomnia/Swagger).
5. Após finalizar a esteira funcional, implemente a suíte de testes unitários exigida.
6. Gerencie os prazos com rigor estrutural. Organização e entregas parciais superam o excesso de complexidade antecipada. Cumpra o escopo base antes de adicionar abstrações desnecessárias.
