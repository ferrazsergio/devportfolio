package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.SocialLink;
import java.util.List;
import java.util.UUID;

public interface SocialLinkService {

    List<SocialLink> list(UUID ownerUserId);

    SocialLink create(UUID ownerUserId, String platform, String url, int order);

    SocialLink update(UUID ownerUserId, UUID socialLinkId, String platform, String url, int order);

    void delete(UUID ownerUserId, UUID socialLinkId);
}
