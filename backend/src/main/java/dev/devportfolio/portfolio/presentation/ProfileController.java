package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
import dev.devportfolio.portfolio.application.ProfileService;
import dev.devportfolio.portfolio.domain.Profile;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileResponse get(@AuthenticationPrincipal AuthenticatedUser principal) {
        Profile profile = profileService.getByOwner(principal.getUser().getId());
        return ProfileResponse.from(profile);
    }

    @PutMapping
    public ProfileResponse update(@AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody UpdateProfileRequest request) {
        Profile profile = profileService.update(principal.getUser().getId(), request.fullName(), request.username(),
                request.photoUrl(), request.headline(), request.bio(), request.location(),
                request.professionalEmail(), request.phone(), request.githubUrl(), request.linkedinUrl(),
                request.websiteUrl());
        return ProfileResponse.from(profile);
    }
}
