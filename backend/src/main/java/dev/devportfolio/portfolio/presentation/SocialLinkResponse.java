package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.portfolio.domain.SocialLink;
import java.util.UUID;

public record SocialLinkResponse(UUID id, String platform, String url, int order) {

    public static SocialLinkResponse from(SocialLink socialLink) {
        return new SocialLinkResponse(socialLink.getId(), socialLink.getPlatform(), socialLink.getUrl(),
                socialLink.getOrder());
    }
}
