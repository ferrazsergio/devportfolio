# DevPortfolio

CMS open source de portfólio profissional para desenvolvedores de software. Rode localmente com Docker Compose, cadastre seu perfil, experiências, projetos, habilidades, formação e certificações, e publique uma página pública (`/{username}`) pronta para divulgar no LinkedIn, WhatsApp ou Twitter/X.

## Como rodar

```bash
git clone https://github.com/ferrazsergio/devportfolio.git
cd devportfolio
cp .env.example .env
docker compose up -d
```

- Frontend: http://localhost:4200
- API (Swagger UI): http://localhost:8080/swagger-ui.html
- Backend (health check): http://localhost:8080/actuator/health
- Adminer (cliente web do banco): http://localhost:8081 — sistema `PostgreSQL`, servidor `postgres`, usuário/senha do `.env`

### Variáveis de ambiente

Todas em `.env.example`, com comentários. As essenciais para rodar localmente já vêm com valores padrão; as demais são opcionais:

| Variável | Obrigatória | Descrição |
|---|---|---|
| `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` | Sim | Credenciais do banco. |
| `APP_CORS_ORIGIN` | Sim | Origem permitida pelo CORS (URL do frontend). |
| `APP_PUBLIC_BASE_URL` | Sim | URL pública usada em SEO/Open Graph e nos links de compartilhamento. |
| `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET` | Não | Habilita "Conectar com GitHub" (importar repositórios como projetos). Exige criar um OAuth App em [github.com/settings/developers](https://github.com/settings/developers) — veja o comentário no `.env.example`. |
| `APP_TOKEN_ENCRYPTION_KEY` | Só se usar GitHub | Chave AES-256 em base64 (`openssl rand -base64 32`) para cifrar o token do GitHub em repouso. |

## Funcionalidades

- Cadastro e login com autenticação por sessão (cookie `HttpOnly`)
- Perfil profissional: nome, foto, headline, bio, contatos e links sociais
- Experiências profissionais, projetos, habilidades (por categoria), formação e certificações
- Página pública de portfólio (`/{username}`) com controle de rascunho/publicado
- SEO automático (title, description, Open Graph, canonical) e preview correto ao compartilhar no LinkedIn, WhatsApp, Facebook ou X
- Importação de repositórios do GitHub como projetos, via OAuth
- Landing page e páginas de admin com design próprio (sem dependência de bibliotecas de UI de terceiros)

## Stack

**Backend:** Java 21, Spring Boot 3, Spring Security (sessão + CSRF), Spring Data JPA, PostgreSQL, Flyway, Resilience4j (circuit breaker), springdoc-openapi, Maven, JUnit 5, Mockito, Testcontainers, WireMock.
**Frontend:** Angular (standalone components, signals, novo control flow `@if`/`@for`), TypeScript, Reactive Forms — sem framework de UI de terceiros, design system próprio.
**Infra:** Docker, Docker Compose, Nginx (reverse proxy + dynamic rendering para SEO), GitHub Actions.

Backend organizado como um monólito modular (não microsserviços), separado por contexto de negócio (identidade, portfólio, experiências, projetos, habilidades, formação, certificações, página pública, integração GitHub), com camadas de domínio, aplicação, infraestrutura e apresentação em cada módulo.

Código (entidades, API, banco de dados) em inglês, seguindo a convenção padrão da indústria; produto (interface, mensagens e conteúdo do portfólio) em português.

## Documentação da API

Com a aplicação rodando, a documentação interativa (Swagger UI) fica em http://localhost:8080/swagger-ui.html, gerada automaticamente a partir dos controllers e DTOs do backend.

## Contribuindo

Contribuições são bem-vindas. Abra uma issue ou pull request.

## Licença

[MIT](LICENSE).
