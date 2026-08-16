package dev.devportfolio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class DevPortfolioApplicationTests {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Test
    void contextLoadsAndMigrationsApply() {
        // Sobe o contexto Spring com um Postgres real (Testcontainers) e aplica as
        // migrations Flyway de fato — prova que o esqueleto da Fase 1 funciona
        // ponta a ponta, sem H2 nem banco em memória (RNF03).
    }
}
