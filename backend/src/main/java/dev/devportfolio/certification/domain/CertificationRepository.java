package dev.devportfolio.certification.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, UUID> {

    List<Certification> findByPortfolioIdOrderByIssueDateDesc(UUID portfolioId);

    Optional<Certification> findByIdAndPortfolioId(UUID id, UUID portfolioId);
}
