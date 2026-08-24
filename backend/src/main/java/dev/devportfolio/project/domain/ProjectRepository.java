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

    // slug já é normalizado para minúsculas pela validação de entrada (regex ^[a-z0-9-]{3,100}$),
    // então uma comparação exata já é suficiente e bate com a unique constraint case-sensitive do banco.
    boolean existsByPortfolioIdAndSlug(UUID portfolioId, String slug);

    boolean existsByPortfolioIdAndSlugAndIdNot(UUID portfolioId, String slug, UUID id);
}
