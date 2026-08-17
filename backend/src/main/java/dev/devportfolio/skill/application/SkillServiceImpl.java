package dev.devportfolio.skill.application;

import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillAlreadyExistsException;
import dev.devportfolio.skill.domain.SkillCategory;
import dev.devportfolio.skill.domain.SkillRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final PortfolioService portfolioService;

    public SkillServiceImpl(SkillRepository skillRepository, PortfolioService portfolioService) {
        this.skillRepository = skillRepository;
        this.portfolioService = portfolioService;
    }

    @Override
    public List<Skill> list(UUID ownerUserId, SkillCategory category) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return category == null ? skillRepository.findByPortfolioId(portfolioId)
                : skillRepository.findByPortfolioIdAndCategory(portfolioId, category);
    }

    @Override
    @Transactional
    public Skill create(UUID ownerUserId, String name, SkillCategory category) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        if (skillRepository.existsByPortfolioIdAndNameIgnoreCase(portfolioId, name)) {
            throw new SkillAlreadyExistsException();
        }
        return skillRepository.save(new Skill(portfolioId, name, category));
    }

    @Override
    @Transactional
    public Skill update(UUID ownerUserId, UUID skillId, String name, SkillCategory category) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Skill skill = skillRepository.findByIdAndPortfolioId(skillId, portfolioId)
                .orElseThrow(() -> new NotFoundException("Habilidade não encontrada."));
        if (skillRepository.existsByPortfolioIdAndNameIgnoreCaseAndIdNot(portfolioId, name, skillId)) {
            throw new SkillAlreadyExistsException();
        }
        skill.update(name, category);
        return skill;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID skillId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Skill skill = skillRepository.findByIdAndPortfolioId(skillId, portfolioId)
                .orElseThrow(() -> new NotFoundException("Habilidade não encontrada."));
        skillRepository.delete(skill);
    }
}
