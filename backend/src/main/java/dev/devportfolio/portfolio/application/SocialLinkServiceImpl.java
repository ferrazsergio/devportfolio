package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.SocialLink;
import dev.devportfolio.portfolio.domain.SocialLinkRepository;
import dev.devportfolio.shared.domain.NotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialLinkServiceImpl implements SocialLinkService {

    private final SocialLinkRepository socialLinkRepository;
    private final PortfolioService portfolioService;
    private final MessageSource messageSource;

    public SocialLinkServiceImpl(SocialLinkRepository socialLinkRepository, PortfolioService portfolioService,
            MessageSource messageSource) {
        this.socialLinkRepository = socialLinkRepository;
        this.portfolioService = portfolioService;
        this.messageSource = messageSource;
    }

    @Override
    public List<SocialLink> list(UUID ownerUserId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return socialLinkRepository.findByPortfolioIdOrderByOrderAsc(portfolioId);
    }

    @Override
    public List<SocialLink> listByPortfolioId(UUID portfolioId) {
        return socialLinkRepository.findByPortfolioIdOrderByOrderAsc(portfolioId);
    }

    @Override
    @Transactional
    public SocialLink create(UUID ownerUserId, String platform, String url, int order) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        return socialLinkRepository.save(new SocialLink(portfolioId, platform, url, order));
    }

    @Override
    @Transactional
    public SocialLink update(UUID ownerUserId, UUID socialLinkId, String platform, String url, int order) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        SocialLink socialLink = socialLinkRepository.findByIdAndPortfolioId(socialLinkId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.socialLink.notFound", null, LocaleContextHolder.getLocale())));
        socialLink.update(platform, url, order);
        return socialLink;
    }

    @Override
    @Transactional
    public void delete(UUID ownerUserId, UUID socialLinkId) {
        UUID portfolioId = portfolioService.requirePortfolioId(ownerUserId);
        SocialLink socialLink = socialLinkRepository.findByIdAndPortfolioId(socialLinkId, portfolioId)
                .orElseThrow(() -> new NotFoundException(
                        messageSource.getMessage("error.socialLink.notFound", null, LocaleContextHolder.getLocale())));
        socialLinkRepository.delete(socialLink);
    }
}
