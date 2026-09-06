package dev.devportfolio.certification.application;

import dev.devportfolio.certification.domain.Certification;
import dev.devportfolio.certification.domain.CertificationRepository;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final PortfolioService portfolioService;
    private final MessageSource messageSource;

    public CertificationServiceImpl(CertificationRepository certificationRepository,
            PortfolioService portfolioService, MessageSource messageSource) {
        this.certificationRepository = certificationRepository;
        this.portfolioService = portfolioService;
        this.messageSource = messageSource;
    }

    @Override
    public List<Certification> list(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return certificationRepository.findByPortfolioIdOrderByIssueDateDesc(portfolioId);
    }

    @Override
    public List<Certification> listByPortfolioId(UUID portfolioId) {
        return certificationRepository.findByPortfolioIdOrderByIssueDateDesc(portfolioId);
    }

    @Override
    @Transactional
    public Certification create(UUID ownerUserId, String name, String issuingOrganization, LocalDate issueDate,
            LocalDate expirationDate, String credentialUrl, String credentialId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return certificationRepository.save(new Certification(portfolioId, name, issuingOrganization, issueDate,
                expirationDate, credentialUrl, credentialId));
    }

    @Override
    @Transactional
    public Certification update(UUID ownerUserId, UUID certificationId, String name, String issuingOrganization,
            LocalDate issueDate, LocalDate expirationDate, String credentialUrl, String credentialId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Certification certification = certificationRepository.findByIdAndPortfolioId(certificationId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.certification.notFound", null, LocaleContextHolder.getLocale())));
        certification.update(name, issuingOrganization, issueDate, expirationDate, credentialUrl, credentialId);
        return certification;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID certificationId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Certification certification = certificationRepository.findByIdAndPortfolioId(certificationId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.certification.notFound", null, LocaleContextHolder.getLocale())));
        certificationRepository.delete(certification);
    }
}
