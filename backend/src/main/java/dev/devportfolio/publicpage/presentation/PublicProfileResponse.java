package dev.devportfolio.publicpage.presentation;

import dev.devportfolio.portfolio.domain.Profile;
import java.util.List;
import java.util.Locale;

public record PublicProfileResponse(String fullName, String headline, String bio, String location,
        String professionalEmail, String phone, String githubUrl, String linkedinUrl, String websiteUrl,
        String photoUrl, List<PublicSocialLinkResponse> socialLinks) {

    public static PublicProfileResponse from(Profile profile, List<PublicSocialLinkResponse> socialLinks,
            Locale locale) {
        boolean english = "en".equals(locale.getLanguage());
        String headline = english && profile.getHeadlineEn() != null ? profile.getHeadlineEn() : profile.getHeadline();
        String bio = english && profile.getBioEn() != null ? profile.getBioEn() : profile.getBio();
        return new PublicProfileResponse(profile.getFullName(), headline, bio,
                profile.getLocation(), profile.getProfessionalEmail(), profile.getPhone(), profile.getGithubUrl(),
                profile.getLinkedinUrl(), profile.getWebsiteUrl(), profile.getPhotoUrl(), socialLinks);
    }
}
