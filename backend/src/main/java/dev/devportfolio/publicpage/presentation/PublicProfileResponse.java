package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.portfolio.domain.Profile;
import java.util.List;

public record PublicProfileResponse(String fullName, String headline, String bio, String location,
        String professionalEmail, String phone, String githubUrl, String linkedinUrl, String websiteUrl,
        String photoUrl, List<PublicSocialLinkResponse> socialLinks) {

    public static PublicProfileResponse from(Profile profile, List<PublicSocialLinkResponse> socialLinks) {
        return new PublicProfileResponse(profile.getFullName(), profile.getHeadline(), profile.getBio(),
                profile.getLocation(), profile.getProfessionalEmail(), profile.getPhone(), profile.getGithubUrl(),
                profile.getLinkedinUrl(), profile.getWebsiteUrl(), profile.getPhotoUrl(), socialLinks);
    }
}
