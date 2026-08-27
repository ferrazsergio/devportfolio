package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
import dev.devportfolio.portfolio.application.SocialLinkService;
import dev.devportfolio.portfolio.domain.SocialLink;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Links sociais")
@RestController
@RequestMapping("/api/v1/social-links")
public class SocialLinkController {

    private final SocialLinkService socialLinkService;

    public SocialLinkController(SocialLinkService socialLinkService) {
        this.socialLinkService = socialLinkService;
    }

    @GetMapping
    public List<SocialLinkResponse> list(@AuthenticationPrincipal AuthenticatedUser principal) {
        return socialLinkService.list(principal.getUser().getId()).stream().map(SocialLinkResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SocialLinkResponse create(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody SocialLinkRequest request) {
        SocialLink socialLink = socialLinkService.create(principal.getUser().getId(), request.platform(),
                request.url(), request.order());
        return SocialLinkResponse.from(socialLink);
    }

    @PutMapping("/{id}")
    public SocialLinkResponse update(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id,
            @Valid @RequestBody SocialLinkRequest request) {
        SocialLink socialLink = socialLinkService.update(principal.getUser().getId(), id, request.platform(),
                request.url(), request.order());
        return SocialLinkResponse.from(socialLink);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser principal, @PathVariable UUID id) {
        socialLinkService.delete(principal.getUser().getId(), id);
    }
}
