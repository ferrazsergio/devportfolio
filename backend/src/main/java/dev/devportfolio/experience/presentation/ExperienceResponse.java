package dev.devportfolio.experience.presentation;

import dev.devportfolio.experience.domain.Experience;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ExperienceResponse(UUID id, String company, String role, String description, LocalDate startDate,
        LocalDate endDate, boolean current, String location, int order, Set<UUID> technologyIds) {

    public static ExperienceResponse from(Experience experience) {
        return new ExperienceResponse(experience.getId(), experience.getCompany(), experience.getRole(),
                experience.getDescription(), experience.getStartDate(), experience.getEndDate(),
                experience.isCurrent(), experience.getLocation(), experience.getOrder(),
                experience.getTechnologyIds());
    }
}
