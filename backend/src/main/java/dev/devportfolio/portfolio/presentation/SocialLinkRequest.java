package dev.devportfolio.portfolio.presentation;

import jakarta.validation.constraints.NotBlank;

public record SocialLinkRequest(
        @NotBlank(message = "Plataforma é obrigatória.") String platform,
        @NotBlank(message = "URL é obrigatória.") String url,
        int order) {
}
