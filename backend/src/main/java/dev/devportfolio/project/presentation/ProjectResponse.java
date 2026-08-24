package dev.devportfolio.project.presentation;

import dev.devportfolio.project.domain.Project;
import dev.devportfolio.project.domain.ProjectStatus;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ProjectResponse(UUID id, String name, String slug, String shortDescription, String fullDescription,
        String imageUrl, String githubUrl, String demoUrl, LocalDate date, ProjectStatus status, boolean featured,
        int order, Set<UUID> technologyIds) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(project.getId(), project.getName(), project.getSlug(),
                project.getShortDescription(), project.getFullDescription(), project.getImageUrl(),
                project.getGithubUrl(), project.getDemoUrl(), project.getDate(), project.getStatus(),
                project.isFeatured(), project.getOrder(), project.getTechnologyIds());
    }
}
