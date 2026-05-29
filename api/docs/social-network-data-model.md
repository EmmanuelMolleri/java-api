# Modelo logico e relacional - Minha Rede Social

## Escopo
Este modelo atende os requisitos de cadastro, autenticacao, feed, curtidas, comentarios, busca de contatos e gestao de amizades.

## Entidades

### app_user
- id (PK, bigint)
- full_name (varchar 255, not null)
- email (varchar 255, not null, unique)
- nickname (varchar 50, null)
- birth_date (date, not null)
- password_hash (varchar 128, not null)
- profile_image (varchar 512, null)
- created_at (timestamp, not null)
- updated_at (timestamp, not null)

### friendship
- id (PK, bigint)
- requester_id (FK -> app_user.id, not null)
- addressee_id (FK -> app_user.id, not null)
- status (varchar 20, not null: PENDING, ACCEPTED, DECLINED)
- created_at (timestamp, not null)
- updated_at (timestamp, not null)

Regras:
- unique(requester_id, addressee_id)
- bloqueio de auto-solicitacao
- para amizade ativa, considerar par em qualquer direcao

### post
- id (PK, bigint)
- author_id (FK -> app_user.id, not null)
- content (text, not null)
- visibility (varchar 20, not null: PUBLIC, PRIVATE)
- created_at (timestamp, not null)
- updated_at (timestamp, not null)

### post_like
- id (PK, bigint)
- post_id (FK -> post.id, not null)
- user_id (FK -> app_user.id, not null)
- created_at (timestamp, not null)

Regras:
- unique(post_id, user_id)

### post_comment
- id (PK, bigint)
- post_id (FK -> post.id, not null)
- author_id (FK -> app_user.id, not null)
- content (varchar 500, not null)
- created_at (timestamp, not null)

## Relacionamentos
- app_user 1:N post
- app_user N:N app_user via friendship (com estado)
- post 1:N post_comment
- post 1:N post_like

## Mapeamento dos requisitos
- Cadastro de usuario: app_user
- Autenticacao: app_user + token JWT (camada aplicacao)
- Feed/dashboard: post + friendship + post_like + post_comment
- Busca de contatos: filtros em app_user
- Gestao de amizades: friendship
- Perfil de terceiros: app_user + post (ACL por visibility e amizade)
