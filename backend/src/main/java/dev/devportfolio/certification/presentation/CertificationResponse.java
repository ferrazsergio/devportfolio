package dev.devportfolio.certification.presentation;

import dev.devportfolio.certification.domain.Certification;
import java.time.LocalDate;
import java.util.UUID;

public record CertificationResponse(UUID id, String name, String issuingOrganization, LocalDate issueDate,
        LocalDate expirationDate, String credentialUrl, String credentialId) {

    public static CertificationResponse from(Certification certification) {
        return new CertificationResponse(certification.getId(), certification.getName(),
                certification.getIssuingOrganization(), certification.getIssueDate(),
                certification.getExpirationDate(), certification.getCredentialUrl(), certification.getCredentialId());
    }
}
