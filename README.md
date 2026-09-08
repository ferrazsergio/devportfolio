# DevPortfolio

CMS open source de portfólio profissional para desenvolvedores de software. Rode localmente com Docker Compose, cadastre seu perfil, experiências, projetos, habilidades, formação e certificações, e publique uma página pública (`/{username}`) pronta para divulgar no LinkedIn, WhatsApp ou Twitter/X.

Self-hosted de verdade: seus dados ficam no seu próprio servidor, sem taxa, sem vendor lock-in, sem plano pago escondido atrás de uma feature básica.

<!--
  TODO: trocar por um GIF curto (10-15s) mostrando o fluxo real — abrir o
  admin, editar o perfil, ver o portfólio público publicado. Ferramentas
  gratuitas pra gravar: ScreenToGif (Windows, exporta GIF direto, ideal pra
  isso) ou OBS Studio (grava em vídeo, melhor pra um demo mais completo).
  <p align="center"><img src="docs/demo.gif" alt="Demo do DevPortfolio" width="800"></p>
-->

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
| `DEEPL_API_KEY` | Não | Habilita a tradução automática (PT→EN) do conteúdo cadastrado pelo usuário — bio, descrições de projetos/experiências. Vazio = feature desligada, nada quebra, só não traduz. Chave gratuita em [deepl.com/pro-api](https://www.deepl.com/pro-api). |
| `DEEPL_API_BASE_URL` | Não | Endpoint da API DeepL. Já vem com o valor do plano free (`api-free.deepl.com`) como padrão. |

## Funcionalidades

**Perfil e conteúdo**
- Perfil profissional completo: nome, foto (upload real), headline, bio, localização guiada (país → estado → cidade) e contato — email e telefone/WhatsApp com seletor de país e máscara internacional
- Links sociais com reconhecimento automático de ~17 plataformas comuns (Twitter/X, Instagram, YouTube, Dev.to, Stack Overflow, Discord etc.), mostrando o ícone de marca real em vez de um ícone genérico
- Experiências, projetos, habilidades (por categoria), formação e certificações — as tecnologias de projetos/experiências referenciam a lista de habilidades já cadastrada, não são texto solto e inconsistente
- Corretor ortográfico próprio (PT/EN) nos campos de texto livre, com sugestões de correção ao clicar na palavra sublinhada
- Importação de repositórios do GitHub como projetos, via OAuth

**Internacionalização**
- Interface bilíngue (PT/EN) com troca instantânea, sem reload da página
- Tradução automática opcional do conteúdo do usuário via DeepL (bio, descrições), mantendo o texto original como fallback quando a tradução não está configurada

**Portfólio público**
- Página pública (`/{username}`) com controle de rascunho/publicado
- SEO automático (title, description, Open Graph, canonical) e preview correto ao compartilhar no LinkedIn, WhatsApp, Facebook ou X, via renderização dedicada para crawlers
- Botão de contato direto (WhatsApp/e-mail) e foto em destaque expansível
- Animações de entrada e rolagem (GSAP): revelação por seção, indicador de navegação que acompanha a leitura, hover magnético nos botões principais

**Produto**
- Tema claro/escuro
- Cadastro e login com autenticação por sessão (cookie `HttpOnly`)
- Landing page e páginas de admin com design próprio, sem dependência de bibliotecas de UI de terceiros

## Stack

**Backend:** Java 21, Spring Boot 3, Spring Security (sessão + CSRF), Spring Data JPA, PostgreSQL, Flyway, Resilience4j (circuit breaker), springdoc-openapi, Maven, JUnit 5, Mockito, Testcontainers, WireMock.
**Frontend:** Angular (standalone components, signals, novo control flow `@if`/`@for`), TypeScript, Reactive Forms, GSAP (animações) — sem framework de UI de terceiros, design system próprio.
**Infra:** Docker, Docker Compose, Nginx (reverse proxy + dynamic rendering para SEO), GitHub Actions.

Backend organizado como um monólito modular (não microsserviços), separado por contexto de negócio (identidade, portfólio, experiências, projetos, habilidades, formação, certificações, página pública, integração GitHub), com camadas de domínio, aplicação, infraestrutura e apresentação em cada módulo.

Código (entidades, API, banco de dados) em inglês, seguindo a convenção padrão da indústria; produto (interface, mensagens e conteúdo do portfólio) bilíngue PT/EN.

## Documentação da API

Com a aplicação rodando, a documentação interativa (Swagger UI) fica em http://localhost:8080/swagger-ui.html, gerada automaticamente a partir dos controllers e DTOs do backend.

## Contribuindo

Contribuições são bem-vindas. Abra uma issue ou pull request.

## Licença

[MIT](LICENSE).
