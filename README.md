# DevPortfolio

CMS open source de portfólio profissional para desenvolvedores de software. Rode localmente com Docker Compose, cadastre seu perfil, experiências, projetos, habilidades, formação e certificações, e publique uma página pública (`/{username}`) pronta para divulgar no LinkedIn.

> **Status:** em desenvolvimento inicial (arquitetura definida). Ainda não há release estável.

## Como rodar

```bash
git clone https://github.com/ferrazsergio/devportfolio.git
cd devportfolio
cp .env.example .env
docker compose up -d
```

- Frontend: http://localhost:4200
- Backend (health check): http://localhost:8080/actuator/health
- Adminer (cliente web do banco): http://localhost:8081 — sistema `PostgreSQL`, servidor `postgres`, usuário/senha do `.env`

## Stack

**Backend:** Java 21, Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, Flyway, Maven, JUnit 5, Mockito, Testcontainers, OpenAPI.
**Frontend:** Angular, TypeScript, Angular Material, Reactive Forms.
**Infra:** Docker, Docker Compose, GitHub Actions.

Backend organizado como um monólito modular (não microsserviços), separado por contexto de negócio (usuário, portfólio, experiências, projetos, habilidades, formação, certificações), com camadas de domínio, aplicação, infraestrutura e apresentação em cada módulo.

Código (entidades, API, banco de dados) em inglês, seguindo a convenção padrão da indústria; produto (interface, mensagens e conteúdo do portfólio) em português.

## Funcionalidades previstas

- Cadastro e autenticação de usuário
- Perfil profissional (bio, headline, contatos, redes sociais)
- Experiências profissionais, projetos, habilidades, formação e certificações
- Página pública de portfólio, com controle de rascunho/publicado
- SEO básico e compartilhamento (incluindo LinkedIn)
- Integração futura com a API do GitHub para importar projetos

## Contribuindo

Contribuições são bem-vindas. Abra uma issue ou pull request. Guia de contribuição detalhado será adicionado conforme o projeto amadurece.

## Licença

[MIT](LICENSE).
