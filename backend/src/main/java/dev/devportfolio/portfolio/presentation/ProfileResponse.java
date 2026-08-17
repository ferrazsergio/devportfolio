package dev.devportfolio.portfolio.presentation;

import dev.devportfolio.portfolio.domain.Profile;

public record ProfileResponse(String fullName, String username, String photoUrl, String headline, String bio,
        String location, String professionalEmail, String phone, String githubUrl, String linkedinUrl,
        String websiteUrl) {

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(profile.getFullName(), profile.getUsername(), profile.getPhotoUrl(),
                profile.getHeadline(), profile.getBio(), profile.getLocation(), profile.getProfessionalEmail(),
                profile.getPhone(), profile.getGithubUrl(), profile.getLinkedinUrl(), profile.getWebsiteUrl());
    }
}
