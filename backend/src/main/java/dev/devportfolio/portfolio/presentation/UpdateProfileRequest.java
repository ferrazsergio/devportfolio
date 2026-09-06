package dev.devportfolio.portfolio.presentation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequest(
        @NotBlank(message = "{validation.profile.fullName.required}") String fullName,

        @NotBlank(message = "{validation.profile.username.required}")
        @Pattern(regexp = "^[a-z0-9-]{3,50}$",
                message = "{validation.profile.username.pattern}")
        String username,

        String headline,
        String bio,
        String location,

        @Email(message = "{validation.profile.professionalEmail.email}") String professionalEmail,

        String phone,
        String githubUrl,
        String linkedinUrl,
        String websiteUrl) {
}
