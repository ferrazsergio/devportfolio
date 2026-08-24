package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.certification.domain.Certification;
import java.time.LocalDate;

public record PublicCertificationResponse(String name, String issuingOrganization, LocalDate issueDate,
        LocalDate expirationDate, String credentialUrl, String credentialId) {

    public static PublicCertificationResponse from(Certification certification) {
        return new PublicCertificationResponse(certification.getName(), certification.getIssuingOrganization(),
                certification.getIssueDate(), certification.getExpirationDate(), certification.getCredentialUrl(),
                certification.getCredentialId());
    }
}
