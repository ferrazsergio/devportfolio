package dev.devportfolio.portfolio.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByPortfolioId(UUID portfolioId);

    // Leitura pública (RF09): username é sempre armazenado em minúsculas, mas um
    // visitante pode digitar a URL com outra caixa — IgnoreCase só faz sentido aqui.
    Optional<Profile> findByUsernameIgnoreCase(String username);

    // username já é normalizado para minúsculas pela validação de entrada (regex ^[a-z0-9-]{3,50}$),
    // então uma comparação exata já é suficiente e bate com a unique constraint case-sensitive do banco.
    boolean existsByUsernameAndPortfolioIdNot(String username, UUID portfolioId);
}
