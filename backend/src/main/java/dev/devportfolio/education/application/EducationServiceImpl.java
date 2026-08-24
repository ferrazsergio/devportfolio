package dev.devportfolio.education.application;

import dev.devportfolio.education.domain.Education;
import dev.devportfolio.education.domain.EducationRepository;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final PortfolioService portfolioService;

    public EducationServiceImpl(EducationRepository educationRepository, PortfolioService portfolioService) {
        this.educationRepository = educationRepository;
        this.portfolioService = portfolioService;
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
        return educationRepository
                .save(new Education(portfolioId, institution, course, degree, startDate, endDate, description));
    }

    @Override
    @Transactional
    public Education update(UUID ownerUserId, UUID educationId, String institution, String course, String degree,
            LocalDate startDate, LocalDate endDate, String description) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Education education = educationRepository.findByIdAndPortfolioId(educationId, portfolioId)
                .orElseThrow(() -> new NotFoundException("Formação não encontrada."));
        education.update(institution, course, degree, startDate, endDate, description);
        return education;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID educationId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Education education = educationRepository.findByIdAndPortfolioId(educationId, portfolioId)
                .orElseThrow(() -> new NotFoundException("Formação não encontrada."));
        educationRepository.delete(education);
    }
}
