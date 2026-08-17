package dev.devportfolio.certification.presentation;

import dev.devportfolio.certification.application.CertificationService;
import dev.devportfolio.certification.domain.Certification;
import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
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

@RestController
@RequestMapping("/api/v1/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping
    public List<CertificationResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return certificationService.list(principal.getUser().getId()).stream().map(CertificationResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CertificationResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CertificationRequest request) {
        Certification certification = certificationService.create(principal.getUser().getId(), request.name(),
                request.issuingOrganization(), request.issueDate(), request.expirationDate(),
                request.credentialUrl(), request.credentialId());
        return CertificationResponse.from(certification);
    }

    @PutMapping("/{id}")
    public CertificationResponse update(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id,
            @Valid @RequestBody CertificationRequest request) {
        Certification certification = certificationService.update(principal.getUser().getId(), id, request.name(),
                request.issuingOrganization(), request.issueDate(), request.expirationDate(),
                request.credentialUrl(), request.credentialId());
        return CertificationResponse.from(certification);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id) {
        certificationService.delete(principal.getUser().getId(), id);
    }
}
