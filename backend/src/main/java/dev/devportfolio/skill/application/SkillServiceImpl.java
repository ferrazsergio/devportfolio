package dev.devportfolio.skill.application;

import dev.devportfolio.portfolio.application.PortfolioService;
import dev.devportfolio.shared.domain.NotFoundException;
import dev.devportfolio.skill.domain.Skill;
import dev.devportfolio.skill.domain.SkillAlreadyExistsException;
import dev.devportfolio.skill.domain.SkillCategory;
import dev.devportfolio.skill.domain.SkillRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final PortfolioService portfolioService;
    private final MessageSource messageSource;

    public SkillServiceImpl(SkillRepository skillRepository, PortfolioService portfolioService,
            MessageSource messageSource) {
        this.skillRepository = skillRepository;
        this.portfolioService = portfolioService;
        this.messageSource = messageSource;
    }

    @Override
    public List<Skill> list(UUID ownerUserId, SkillCategory category) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return category == null ? skillRepository.findByPortfolioId(portfolioId)
                : skillRepository.findByPortfolioIdAndCategory(portfolioId, category);
    }

    @Override
    public List<Skill> listByPortfolioId(UUID portfolioId) {
        return skillRepository.findByPortfolioId(portfolioId);
    }

    @Override
    @Transactional
    public Skill create(UUID ownerUserId, String name, SkillCategory category) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        if (skillRepository.existsByPortfolioIdAndNameIgnoreCase(portfolioId, name)) {
            throw new SkillAlreadyExistsException(
                    messageSource.getMessage("error.skill.alreadyExists", null, LocaleContextHolder.getLocale()));
        }
        return skillRepository.save(new Skill(portfolioId, name, category));
    }

    @Override
    @Transactional
    public Skill update(UUID ownerUserId, UUID skillId, String name, SkillCategory category) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Skill skill = skillRepository.findByIdAndPortfolioId(skillId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.skill.notFound", null, LocaleContextHolder.getLocale())));
        if (skillRepository.existsByPortfolioIdAndNameIgnoreCaseAndIdNot(portfolioId, name, skillId)) {
            throw new SkillAlreadyExistsException(
                    messageSource.getMessage("error.skill.alreadyExists", null, LocaleContextHolder.getLocale()));
        }
        skill.update(name, category);
        return skill;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID skillId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        Skill skill = skillRepository.findByIdAndPortfolioId(skillId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.skill.notFound", null, LocaleContextHolder.getLocale())));
        skillRepository.delete(skill);
    }

    @Override
    public List<Skill> findByPortfolioIdAndIdIn(UUID portfolioId, Set<UUID> skillIds) {
        if (skillIds.isEmpty()) {
            return List.of();
        }
        return skillRepository.findByPortfolioIdAndIdIn(portfolioId, skillIds);
    }
}
