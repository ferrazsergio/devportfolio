package dev.devportfolio.project.application;

import dev.devportfolio.project.domain.Project;
import dev.devportfolio.project.domain.ProjectStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProjectService {

    List<Project> list(UUID ownerUserId, Boolean featured);

    Project create(UUID ownerUserId, String name, String slug, String shortDescription, String fullDescription,
            String imageUrl, String githubUrl, String demoUrl, LocalDate date, ProjectStatus status,
            boolean featured, Set<UUID> technologyIds);

    Project update(UUID ownerUserId, UUID projectId, String name, String slug, String shortDescription,
            String fullDescription, String imageUrl, String githubUrl, String demoUrl, LocalDate date,
            ProjectStatus status, boolean featured, Set<UUID> technologyIds);

    void delete(UUID ownerUserId, UUID projectId);

    void reorder(UUID ownerUserId, List<UUID> orderedIds);
}
