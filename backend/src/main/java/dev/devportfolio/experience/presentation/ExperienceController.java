package dev.devportfolio.experience.presentation;

import dev.devportfolio.experience.application.ExperienceService;
import dev.devportfolio.experience.domain.Experience;
import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Experiências")
@RestController
@RequestMapping("/api/v1/experiences")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @GetMapping
    public List<ExperienceResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return experienceService.list(principal.getUser().getId()).stream().map(ExperienceResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienceResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody ExperienceRequest request) {
        Experience experience = experienceService.create(principal.getUser().getId(), request.company(),
                request.role(), request.description(), request.startDate(), request.endDate(), request.current(),
                request.location(), request.technologyIds());
        return ExperienceResponse.from(experience);
    }

    @PutMapping("/{id}")
    public ExperienceResponse update(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id,
            @Valid @RequestBody ExperienceRequest request) {
        Experience experience = experienceService.update(principal.getUser().getId(), id, request.company(),
                request.role(), request.description(), request.startDate(), request.endDate(), request.current(),
                request.location(), request.technologyIds());
        return ExperienceResponse.from(experience);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id) {
        experienceService.delete(principal.getUser().getId(), id);
    }

    @PatchMapping("/reorder")
    public void reorder(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody ReorderRequest request) {
        experienceService.reorder(principal.getUser().getId(), request.orderedIds());
    }
}
