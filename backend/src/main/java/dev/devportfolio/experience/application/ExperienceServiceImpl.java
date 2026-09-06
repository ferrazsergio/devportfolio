package dev.devportfolio.experience.application;

import dev.devportfolio.experience.domain.Experience;
import dev.devportfolio.experience.domain.ExperienceRepository;
import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.skill.application.SkillService;
import dev.devportfolio.translation.application.ContentTranslationService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final PortfolioService portfolioService;
    private final SkillService skillService;
    private final MessageSource messageSource;
    private final ContentTranslationService translationService;

    public ExperienceServiceImpl(ExperienceRepository experienceRepository, PortfolioService portfolioService,
            SkillService skillService, MessageSource messageSource, ContentTranslationService translationService) {
        this.experienceRepository = experienceRepository;
        this.portfolioService = portfolioService;
        this.skillService = skillService;
        this.messageSource = messageSource;
        this.translationService = translationService;
    }

    @Override
    public List<Experience> list(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return experienceRepository.findByPortfolioIdOrderByOrderAsc(portfolioId);
    }

    @Override
    public List<Experience> listByPortfolioId(UUID portfolioId) {
        return experienceRepository.findByPortfolioIdOrderByOrderAsc(portfolioId);
    }

    @Override
    @Transactional
    public Experience create(UUID ownerUserId, String company, String role, String description, LocalDate startDate,
            LocalDate endDate, boolean current, String location, Set<UUID> technologyIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        requireOwnedTechnologies(portfolioId, technologyIds);
        int nextOrder = experienceRepository.findByPortfolioIdOrderByOrderAsc(portfolioId).size();
        String descriptionEn = translationService.translateToEnglish(description).orElse(null);
        return experienceRepository.save(new Experience(portfolioId, company, role, description, descriptionEn,
                startDate, endDate, current, location, nextOrder, technologyIds));
    }

    @Override
    @Transactional
    public Experience update(UUID ownerUserId, UUID experienceId, String company, String role, String description,
            LocalDate startDate, LocalDate endDate, boolean current, String location, Set<UUID> technologyIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        requireOwnedTechnologies(portfolioId, technologyIds);
        Experience experience = experienceRepository.findByIdAndPortfolioId(experienceId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.experience.notFound", null, LocaleContextHolder.getLocale())));
        String descriptionEn = translationService.translateToEnglish(description).orElse(null);
        experience.update(company, role, description, descriptionEn, startDate, endDate, current, location,
                technologyIds);
        return experience;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID experienceId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Experience experience = experienceRepository.findByIdAndPortfolioId(experienceId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.experience.notFound", null, LocaleContextHolder.getLocale())));
        experienceRepository.delete(experience);
    }

    @Override
    @Transactional
    public void reorder(UUID ownerUserId, List<UUID> orderedIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        for (int index = 0; index < orderedIds.size(); index++) {
            Experience experience = experienceRepository.findByIdAndPortfolioId(orderedIds.get(index), portfolioId)
                    .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.experience.notFound", null, LocaleContextHolder.getLocale())));
            experience.reorder(index);
        }
    }

    private void requireOwnedTechnologies(UUID portfolioId, Set<UUID> technologyIds) {
        if (technologyIds.isEmpty()) {
            return;
        }
        if (skillService.findByPortfolioIdAndIdIn(portfolioId, technologyIds).size() != technologyIds.size()) {
            throw new NotFoundException(
                    messageSource.getMessage("error.experience.skillsNotFound", null, LocaleContextHolder.getLocale()));
        }
    }
}
