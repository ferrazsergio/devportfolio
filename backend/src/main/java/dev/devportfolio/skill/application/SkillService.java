package dev.devportfolio.skill.application;

import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillCategory;
import java.util.List;
import java.util.UUID;

public interface SkillService {

    List<Skill> list(UUID ownerUserId, SkillCategory category);

    Skill create(UUID ownerUserId, String name, SkillCategory category);

    Skill update(UUID ownerUserId, UUID skillId, String name, SkillCategory category);

    void delete(UUID ownerUserId, UUID skillId);
}
