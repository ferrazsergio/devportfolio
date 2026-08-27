package dev.devportfolio.project.presentation;

import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
import dev.devportfolio.project.application.ProjectService;
import dev.devportfolio.project.domain.Project;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Projetos")
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> list(@AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) Boolean featured) {
        return projectService.list(principal.getUser().getId(), featured).stream().map(ProjectResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody ProjectRequest request) {
        Project project = projectService.create(principal.getUser().getId(), request.name(), request.slug(),
                request.shortDescription(), request.fullDescription(), request.imageUrl(), request.githubUrl(),
                request.demoUrl(), request.date(), request.status(), request.featured(), request.technologyIds());
        return ProjectResponse.from(project);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id,
            @Valid @RequestBody ProjectRequest request) {
        Project project = projectService.update(principal.getUser().getId(), id, request.name(), request.slug(),
                request.shortDescription(), request.fullDescription(), request.imageUrl(), request.githubUrl(),
                request.demoUrl(), request.date(), request.status(), request.featured(), request.technologyIds());
        return ProjectResponse.from(project);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id) {
        projectService.delete(principal.getUser().getId(), id);
    }

    @PatchMapping("/reorder")
    public void reorder(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody ReorderRequest request) {
        projectService.reorder(principal.getUser().getId(), request.orderedIds());
    }
}
