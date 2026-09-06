package dev.devportfolio.certification.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CertificationRequest(
        @NotBlank(message = "{validation.certification.name.required}") String name,
        @NotBlank(message = "{validation.certification.issuingOrganization.required}") String issuingOrganization,
        @NotNull(message = "{validation.certification.issueDate.required}") LocalDate issueDate,
        LocalDate expirationDate,
        String credentialUrl,
        String credentialId) {
}
