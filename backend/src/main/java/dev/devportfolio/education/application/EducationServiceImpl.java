package dev.devportfolio.education.application;

import dev.devportfolio.education.domain.Education;
import dev.devportfolio.education.domain.EducationRepository;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.translation.application.ContentTranslationService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final PortfolioService portfolioService;
    private final MessageSource messageSource;
    private final ContentTranslationService translationService;

    public EducationServiceImpl(EducationRepository educationRepository, PortfolioService portfolioService,
            MessageSource messageSource, ContentTranslationService translationService) {
        this.educationRepository = educationRepository;
        this.portfolioService = portfolioService;
        this.messageSource = messageSource;
        this.translationService = translationService;
    }

    @Override
    public List<Education> list(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return educationRepository.findByPortfolioIdOrderByStartDateDesc(portfolioId);
    }

    @Override
    public List<Education> listByPortfolioId(UUID portfolioId) {
        return educationRepository.findByPortfolioIdOrderByStartDateDesc(portfolioId);
    }

    @Override
    @Transactional
    public Education create(UUID ownerUserId, String institution, String course, String degree, LocalDate startDate,
            LocalDate endDate, String description) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        String descriptionEn = translationService.translateToEnglish(description).orElse(null);
        return educationRepository.save(
                new Education(portfolioId, institution, course, degree, startDate, endDate, description,
                        descriptionEn));
    }

    @Override
    @Transactional
    public Education update(UUID ownerUserId, UUID educationId, String institution, String course, String degree,
            LocalDate startDate, LocalDate endDate, String description) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Education education = educationRepository.findByIdAndPortfolioId(educationId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.education.notFound", null, LocaleContextHolder.getLocale())));
        String descriptionEn = translationService.translateToEnglish(description).orElse(null);
        education.update(institution, course, degree, startDate, endDate, description, descriptionEn);
        return education;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID educationId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Education education = educationRepository.findByIdAndPortfolioId(educationId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.education.notFound", null, LocaleContextHolder.getLocale())));
        educationRepository.delete(education);
    }
}
