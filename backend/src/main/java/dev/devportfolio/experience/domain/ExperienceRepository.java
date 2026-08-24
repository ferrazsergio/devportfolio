package dev.devportfolio.experience.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, UUID> {

    @EntityGraph(attributePaths = "technologyIds")
    List<Experience> findByPortfolioIdOrderByOrderAsc(UUID portfolioId);

    Optional<Experience> findByIdAndPortfolioId(UUID id, UUID portfolioId);
}
