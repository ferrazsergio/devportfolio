package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Profile;
import java.util.UUID;

public interface ProfileService {

    Profile getByOwner(UUID ownerUserId);

    Profile update(UUID ownerUserId, String fullName, String username, String photoUrl, String headline, String bio,
            String location, String professionalEmail, String phone, String githubUrl, String linkedinUrl,
            String websiteUrl);
}
