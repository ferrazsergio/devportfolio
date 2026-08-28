package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.identity.infrastructure.AuthenticatedUser;
import dev.devportfolio.portfolio.application.ProfileService;
import dev.devportfolio.portfolio.domain.Profile;
import dev.devportfolio.portfolio.infrastructure.ProfilePhotoStorage;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Perfil")
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;
    private final ProfilePhotoStorage photoStorage;
    private final String publicBaseUrl;

    public ProfileController(ProfileService profileService, ProfilePhotoStorage photoStorage,
            @Value("${app.public-base-url}") String publicBaseUrl) {
        this.profileService = profileService;
        this.photoStorage = photoStorage;
        this.publicBaseUrl = publicBaseUrl;
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
                request.headline(), request.bio(), request.location(),
                request.professionalEmail(), request.phone(), request.githubUrl(), request.linkedinUrl(),
                request.websiteUrl());
        return ProfileResponse.from(profile);
    }

    @PostMapping("/photo")
    public ProfileResponse uploadPhoto(@AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("file") MultipartFile file) {
        Profile profile = profileService.getByOwner(principal.getUser().getId());
        String previousPhotoUrl = profile.getPhotoUrl();

        String filename = photoStorage.store(file);
        String photoUrl = publicBaseUrl + "/api/v1/public/files/" + filename;
        Profile updated = profileService.updatePhoto(principal.getUser().getId(), photoUrl);

        deleteStoredFile(previousPhotoUrl);
        return ProfileResponse.from(updated);
    }

    @DeleteMapping("/photo")
    public ProfileResponse removePhoto(@AuthenticationPrincipal AuthenticatedUser principal) {
        Profile profile = profileService.getByOwner(principal.getUser().getId());
        String previousPhotoUrl = profile.getPhotoUrl();

        Profile updated = profileService.updatePhoto(principal.getUser().getId(), null);

        deleteStoredFile(previousPhotoUrl);
        return ProfileResponse.from(updated);
    }

    private void deleteStoredFile(String photoUrl) {
        if (photoUrl == null) {
            return;
        }
        String filename = photoUrl.substring(photoUrl.lastIndexOf('/') + 1);
        photoStorage.delete(filename);
    }
}
