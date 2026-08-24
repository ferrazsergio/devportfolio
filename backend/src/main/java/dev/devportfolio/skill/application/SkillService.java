package dev.devportfolio.skill.application;

import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillCategory;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SkillService {

    List<Skill> list(UUID ownerUserId, SkillCategory category);

    /** Usada pelo módulo publicpage (RF09) — portfolioId já resolvido pelo chamador. */
    List<Skill> listByPortfolioId(UUID portfolioId);

    Skill create(UUID ownerUserId, String name, SkillCategory category);

    Skill update(UUID ownerUserId, UUID skillId, String name, SkillCategory category);

    void delete(UUID ownerUserId, UUID skillId);

    /**
     * Porta usada por outros módulos (experience, project) para validar que um
     * conjunto de ids de skill realmente pertence ao portfólio informado, antes
     * de associá-los a um recurso — evita referenciar uma skill de outro tenant.
     * Recebe portfolioId (não ownerUserId) porque o módulo chamador já o
     * resolveu para o próprio recurso; evita uma segunda consulta redundante.
     */
    List<Skill> findByPortfolioIdAndIdIn(UUID portfolioId, Set<UUID> skillIds);
}
