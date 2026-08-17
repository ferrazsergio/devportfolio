package dev.devportfolio.portfolio.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialLinkRepository extends JpaRepository<SocialLink, UUID> {

    List<SocialLink> findByPortfolioIdOrderByOrderAsc(UUID portfolioId);

    Optional<SocialLink> findByIdAndPortfolioId(UUID id, UUID portfolioId);
}
