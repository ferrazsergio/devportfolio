package dev.devportfolio.certification.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CertificationRequest(
        @NotBlank(message = "Nome é obrigatório.") String name,
        @NotBlank(message = "Instituição emissora é obrigatória.") String issuingOrganization,
        @NotNull(message = "Data de emissão é obrigatória.") LocalDate issueDate,
        LocalDate expirationDate,
        String credentialUrl,
        String credentialId) {
}
