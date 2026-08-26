package dev.devportfolio.github.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubConnectionRepository extends JpaRepository<GithubConnection, UUID> {

    Optional<GithubConnection> findByPortfolioId(UUID portfolioId);

    void deleteByPortfolioId(UUID portfolioId);
}
