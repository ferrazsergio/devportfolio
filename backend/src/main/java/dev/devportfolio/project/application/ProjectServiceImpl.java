package dev.devportfolio.project.application;

import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.project.domain.Project;
import dev.devportfolio.project.domain.ProjectRepository;
import dev.devportfolio.project.domain.ProjectSlugAlreadyInUseException;
import dev.devportfolio.project.domain.ProjectStatus;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.skill.application.SkillService;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final PortfolioService portfolioService;
    private final SkillService skillService;

    public ProjectServiceImpl(ProjectRepository projectRepository, PortfolioService portfolioService,
            SkillService skillService) {
        this.projectRepository = projectRepository;
        this.portfolioService = portfolioService;
        this.skillService = skillService;
    }

    @Override
    public List<Project> list(UUID ownerUserId, Boolean featured) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return featured == null ? projectRepository.findByPortfolioIdOrderByOrderAsc(portfolioId)
                : projectRepository.findByPortfolioIdAndFeaturedOrderByOrderAsc(portfolioId, featured);
    }

    @Override
    @Transactional
    public Project create(UUID ownerUserId, String name, String slug, String shortDescription,
            String fullDescription, String imageUrl, String githubUrl, String demoUrl, LocalDate date,
            ProjectStatus status, boolean featured, Set<UUID> technologyIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        requireOwnedTechnologies(portfolioId, technologyIds);
        if (projectRepository.existsByPortfolioIdAndSlug(portfolioId, slug)) {
            throw new ProjectSlugAlreadyInUseException();
        }
        int nextOrder = projectRepository.findByPortfolioIdOrderByOrderAsc(portfolioId).size();
        return projectRepository.save(new Project(portfolioId, name, slug, shortDescription, fullDescription,
                imageUrl, githubUrl, demoUrl, date, status, featured, nextOrder, technologyIds));
    }

    @Override
    @Transactional
    public Project update(UUID ownerUserId, UUID projectId, String name, String slug, String shortDescription,
            String fullDescription, String imageUrl, String githubUrl, String demoUrl, LocalDate date,
            ProjectStatus status, boolean featured, Set<UUID> technologyIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        requireOwnedTechnologies(portfolioId, technologyIds);
        Project project = projectRepository.findByIdAndPortfolioId(projectId, portfolioId)
                .orElseThrow(() -> new NotFoundException("Projeto não encontrado."));
        if (projectRepository.existsByPortfolioIdAndSlugAndIdNot(portfolioId, slug, projectId)) {
            throw new ProjectSlugAlreadyInUseException();
        }
        project.update(name, slug, shortDescription, fullDescription, imageUrl, githubUrl, demoUrl, date, status,
                featured, technologyIds);
        return project;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID projectId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Project project = projectRepository.findByIdAndPortfolioId(projectId, portfolioId)
                .orElseThrow(() -> new NotFoundException("Projeto não encontrado."));
        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public void reorder(UUID ownerUserId, List<UUID> orderedIds) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        for (int index = 0; index < orderedIds.size(); index++) {
            Project project = projectRepository.findByIdAndPortfolioId(orderedIds.get(index), portfolioId)
                    .orElseThrow(() -> new NotFoundException("Projeto não encontrado."));
            project.reorder(index);
        }
    }

    private void requireOwnedTechnologies(UUID portfolioId, Set<UUID> technologyIds) {
        if (technologyIds.isEmpty()) {
            return;
        }
        if (skillService.findByPortfolioIdAndIdIn(portfolioId, technologyIds).size() != technologyIds.size()) {
            throw new NotFoundException("Uma ou mais habilidades informadas não foram encontradas.");
        }
    }
}
