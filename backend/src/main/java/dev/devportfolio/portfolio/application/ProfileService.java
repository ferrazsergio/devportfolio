package dev.devportfolio.portfolio.application;

import dev.devportfolio.portfolio.domain.Profile;
import java.util.Optional;
import java.util.UUID;

public interface ProfileService {

    Profile getByOwner(UUID ownerUserId);

    /** Usada pelo módulo publicpage (RF09) — não exige autenticação/posse. */
    Optional<Profile> findByUsername(String username);

    Profile update(UUID ownerUserId, String fullName, String username, String photoUrl, String headline, String bio,
            String location, String professionalEmail, String phone, String githubUrl, String linkedinUrl,
            String websiteUrl);
}
