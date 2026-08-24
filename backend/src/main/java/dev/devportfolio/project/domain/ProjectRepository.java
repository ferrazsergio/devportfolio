package dev.devportfolio.project.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @EntityGraph(attributePaths = "technologyIds")
    List<Project> findByPortfolioIdOrderByOrderAsc(UUID portfolioId);

    @EntityGraph(attributePaths = "technologyIds")
    List<Project> findByPortfolioIdAndFeaturedOrderByOrderAsc(UUID portfolioId, boolean featured);

    Optional<Project> findByIdAndPortfolioId(UUID id, UUID portfolioId);

    boolean existsByPortfolioIdAndSlugIgnoreCase(UUID portfolioId, String slug);

    boolean existsByPortfolioIdAndSlugIgnoreCaseAndIdNot(UUID portfolioId, String slug, UUID id);
}
