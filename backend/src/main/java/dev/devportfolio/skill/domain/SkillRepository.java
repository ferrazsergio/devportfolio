package dev.devportfolio.skill.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, UUID> {

    List<Skill> findByPortfolioId(UUID portfolioId);

    List<Skill> findByPortfolioIdAndCategory(UUID portfolioId, SkillCategory category);

    Optional<Skill> findByIdAndPortfolioId(UUID id, UUID portfolioId);

    boolean existsByPortfolioIdAndNameIgnoreCase(UUID portfolioId, String name);

    boolean existsByPortfolioIdAndNameIgnoreCaseAndIdNot(UUID portfolioId, String name, UUID id);
}
