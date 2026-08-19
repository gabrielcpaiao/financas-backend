# financas-backend

API REST do sistema de controle financeiro. Spring Boot 3 + MySQL + Flyway + JWT.

## Rodando localmente

1. Suba um MySQL local (ou use Docker: `docker run -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8`).
2. Configure as variáveis de ambiente (ou edite `application.yml` diretamente):
   - `DB_USERNAME`, `DB_PASSWORD`
   - `JWT_SECRET` (string longa e aleatória)
   - `CORS_ALLOWED_ORIGINS` (default: `http://localhost:3000`)
3. Rode: `./mvnw spring-boot:run`
4. O Flyway cria o schema automaticamente na primeira subida (migration `V1__initial_schema.sql`).
5. Swagger UI: http://localhost:8080/swagger-ui.html

## Padrão de camadas

Veja `Category` (domain/repository/dto/mapper/service/controller) como referência para
implementar os próximos recursos: `account`, `financial_transaction`, `credit_card`,
`monthly_budget`, `investment`, `planned_purchase` — nessa ordem, seguindo o corte definido
em `escopo-mvp.md`.

Toda query de service deve filtrar pelo usuário autenticado via `AuthenticatedUser.currentUserId()`
— nunca confiar em um `userId` vindo do corpo da requisição.
