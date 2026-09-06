package dev.devportfolio.portfolio.presentation;

import jakarta.validation.constraints.NotBlank;

public record SocialLinkRequest(
        @NotBlank(message = "{validation.socialLink.platform.required}") String platform,
        @NotBlank(message = "{validation.socialLink.url.required}") String url,
        int order) {
}
