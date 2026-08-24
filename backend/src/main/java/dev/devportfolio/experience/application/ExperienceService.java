package dev.devportfolio.experience.application;

import dev.devportfolio.experience.domain.Experience;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ExperienceService {

    List<Experience> list(UUID ownerUserId);

    Experience create(UUID ownerUserId, String company, String role, String description, LocalDate startDate,
            LocalDate endDate, boolean current, String location, Set<UUID> technologyIds);

    Experience update(UUID ownerUserId, UUID experienceId, String company, String role, String description,
            LocalDate startDate, LocalDate endDate, boolean current, String location, Set<UUID> technologyIds);

    void delete(UUID ownerUserId, UUID experienceId);

    void reorder(UUID ownerUserId, List<UUID> orderedIds);
}
