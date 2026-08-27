package dev.devportfolio.skill.presentation;

import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
import dev.devportfolio.skill.application.SkillService;
import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Habilidades")
@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public List<SkillResponse> list(@AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam(required = false) SkillCategory category) {
        return skillService.list(principal.getUser().getId(), category).stream().map(SkillResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody SkillRequest request) {
        Skill skill = skillService.create(principal.getUser().getId(), request.name(), request.category());
        return SkillResponse.from(skill);
    }

    @PutMapping("/{id}")
    public SkillResponse update(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id,
            @Valid @RequestBody SkillRequest request) {
        Skill skill = skillService.update(principal.getUser().getId(), id, request.name(), request.category());
        return SkillResponse.from(skill);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id) {
        skillService.delete(principal.getUser().getId(), id);
    }
}
