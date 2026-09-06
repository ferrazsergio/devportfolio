package dev.devportfolio.project.application;

import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.project.domain.Project;
import dev.devportfolio.project.domain.ProjectRepository;
import dev.devportfolio.project.domain.ProjectSlugAlreadyInUseException;
import dev.devportfolio.project.domain.ProjectStatus;
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
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final PortfolioService portfolioService;
    private final SkillService skillService;
    private final MessageSource messageSource;
    private final ContentTranslationService translationService;

    public ProjectServiceImpl(ProjectRepository projectRepository, PortfolioService portfolioService,
            SkillService skillService, MessageSource messageSource, ContentTranslationService translationService) {
        this.projectRepository = projectRepository;
        this.portfolioService = portfolioService;
        this.skillService = skillService;
        this.messageSource = messageSource;
        this.translationService = translationService;
    }

    @Override
    public List<Project> list(UUID ownerUserId, Boolean featured) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return featured == null ? projectRepository.findByPortfolioIdOrderByOrderAsc(portfolioId)
                : projectRepository.findByPortfolioIdAndFeaturedOrderByOrderAsc(portfolioId, featured);
    }

    @Override
    public List<Project> listByPortfolioId(UUID portfolioId) {
        return projectRepository.findByPortfolioIdOrderByOrderAsc(portfolioId);
    }

    @Override
    @Transactional
    public Project create(UUID ownerUserId, String name, String slug, String shortDescription,
            String fullDescription, String imageUrl, String githubUrl, String demoUrl, LocalDate date,
            ProjectStatus status, boolean featured, Set<UUID> technologyIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        requireOwnedTechnologies(portfolioId, technologyIds);
        if (projectRepository.existsByPortfolioIdAndSlug(portfolioId, slug)) {
            throw new ProjectSlugAlreadyInUseException(
                    messageSource.getMessage("error.project.slugAlreadyInUse", null, LocaleContextHolder.getLocale()));
        }
        int nextOrder = projectRepository.findByPortfolioIdOrderByOrderAsc(portfolioId).size();
        String shortDescriptionEn = translationService.translateToEnglish(shortDescription).orElse(null);
        String fullDescriptionEn = translationService.translateToEnglish(fullDescription).orElse(null);
        return projectRepository.save(new Project(portfolioId, name, slug, shortDescription, shortDescriptionEn,
                fullDescription, fullDescriptionEn, imageUrl, githubUrl, demoUrl, date, status, featured, nextOrder,
                technologyIds));
    }

    @Override
    @Transactional
    public Project update(UUID ownerUserId, UUID projectId, String name, String slug, String shortDescription,
            String fullDescription, String imageUrl, String githubUrl, String demoUrl, LocalDate date,
            ProjectStatus status, boolean featured, Set<UUID> technologyIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        requireOwnedTechnologies(portfolioId, technologyIds);
        Project project = projectRepository.findByIdAndPortfolioId(projectId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.project.notFound", null, LocaleContextHolder.getLocale())));
        if (projectRepository.existsByPortfolioIdAndSlugAndIdNot(portfolioId, slug, projectId)) {
            throw new ProjectSlugAlreadyInUseException(
                    messageSource.getMessage("error.project.slugAlreadyInUse", null, LocaleContextHolder.getLocale()));
        }
        String shortDescriptionEn = translationService.translateToEnglish(shortDescription).orElse(null);
        String fullDescriptionEn = translationService.translateToEnglish(fullDescription).orElse(null);
        project.update(name, slug, shortDescription, shortDescriptionEn, fullDescription, fullDescriptionEn, imageUrl,
                githubUrl, demoUrl, date, status, featured, technologyIds);
        return project;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID projectId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Project project = projectRepository.findByIdAndPortfolioId(projectId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.project.notFound", null, LocaleContextHolder.getLocale())));
        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public void reorder(UUID ownerUserId, List<UUID> orderedIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        for (int index = 0; index < orderedIds.size(); index++) {
            Project project = projectRepository.findByIdAndPortfolioId(orderedIds.get(index), portfolioId)
                    .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.project.notFound", null, LocaleContextHolder.getLocale())));
            project.reorder(index);
        }
    }

    private void requireOwnedTechnologies(UUID portfolioId, Set<UUID> technologyIds) {
        if (technologyIds.isEmpty()) {
            return;
        }
        if (skillService.findByPortfolioIdAndIdIn(portfolioId, technologyIds).size() != technologyIds.size()) {
            throw new NotFoundException(
                    messageSource.getMessage("error.project.skillsNotFound", null, LocaleContextHolder.getLocale()));
        }
    }
}
