package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.portfolio.domain.SocialLink;

public record PublicSocialLinkResponse(String platform, String url, int order) {

    public static PublicSocialLinkResponse from(SocialLink socialLink) {
        return new PublicSocialLinkResponse(socialLink.getPlatform(), socialLink.getUrl(), socialLink.getOrder());
    }
}
