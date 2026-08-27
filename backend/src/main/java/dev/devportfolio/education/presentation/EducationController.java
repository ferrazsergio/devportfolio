package dev.devportfolio.education.presentation;

import dev.devportfolio.education.application.EducationService;
import dev.devportfolio.education.domain.Education;
import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Formação")
@RestController
@RequestMapping("/api/v1/educations")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @GetMapping
    public List<EducationResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return educationService.list(principal.getUser().getId()).stream().map(EducationResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EducationResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody EducationRequest request) {
        Education education = educationService.create(principal.getUser().getId(), request.institution(),
                request.course(), request.degree(), request.startDate(), request.endDate(), request.description());
        return EducationResponse.from(education);
    }

    @PutMapping("/{id}")
    public EducationResponse update(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id,
            @Valid @RequestBody EducationRequest request) {
        Education education = educationService.update(principal.getUser().getId(), id, request.institution(),
                request.course(), request.degree(), request.startDate(), request.endDate(), request.description());
        return EducationResponse.from(education);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id) {
        educationService.delete(principal.getUser().getId(), id);
    }
}
