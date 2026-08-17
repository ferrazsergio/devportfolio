package dev.devportfolio.portfolio.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

    Optional<Profile> findByPortfolioId(UUID portfolioId);

    // username já é normalizado para minúsculas pela validação de entrada (regex ^[a-z0-9-]{3,50}$),
    // então uma comparação exata já é suficiente e bate com a unique constraint case-sensitive do banco.
    boolean existsByUsernameAndPortfolioIdNot(String username, UUID portfolioId);
}
