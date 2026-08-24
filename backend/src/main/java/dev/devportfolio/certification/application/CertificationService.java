package dev.devportfolio.certification.application;

import dev.devportfolio.certification.domain.Certification;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface CertificationService {

    List<Certification> list(UUID ownerUserId);

    /** Usada pelo módulo publicpage (RF09) — portfolioId já resolvido pelo chamador. */
    List<Certification> listByPortfolioId(UUID portfolioId);

    Certification create(UUID ownerUserId, String name, String issuingOrganization, LocalDate issueDate,
            LocalDate expirationDate, String credentialUrl, String credentialId);

    Certification update(UUID ownerUserId, UUID certificationId, String name, String issuingOrganization,
            LocalDate issueDate, LocalDate expirationDate, String credentialUrl, String credentialId);

    void delete(UUID ownerUserId, UUID certificationId);
}
