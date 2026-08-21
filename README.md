# Financas Backend

Backend (API REST) do meu sistema de controle financeiro. Spring Boot 3 + MySQL + Flyway + JWT.

## Comandos:
1. mvn clean compile
2. mvn spring-boot:run
3. mvn clean test (Apaga a pasta de compilação antiga (target) do projeto e, em seguida, compila o código-fonte atual para executar todos os testes unitários configurados, garantindo que o processo ocorra em um ambiente totalmente limpo de arquivos residuais)

## Rodando localmente

1. Configure as variáveis de ambiente (ou edite `application.yml` diretamente):
   - Em spring -> datasource `username`, `password`
   - `JWT_SECRET` (string longa e aleatória)
   - `CORS_ALLOWED_ORIGINS` (default: `http://localhost:3000`)
2. Rode: `./mvnw spring-boot:run`
3. O Flyway cria o schema automaticamente na primeira subida (migration `V1__initial_schema.sql`).
4. Swagger UI: http://localhost:8080/swagger-ui.html

## Padrão de camadas

Veja `Category` (domain/repository/dto/mapper/service/controller) como referência para
implementar os próximos recursos.

Toda query de service deve filtrar pelo usuário autenticado via `AuthenticatedUser.currentUserId()`
— nunca confiar em um `userId` vindo do corpo da requisição.
