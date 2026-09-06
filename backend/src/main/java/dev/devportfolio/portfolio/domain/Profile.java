package dev.devportfolio.portfolio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * 1:1 com Portfolio. Criado vazio (todos os campos além de portfolioId nulos)
 * junto com o Portfolio no registro do usuário (ver docs/03 §2).
 */
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "portfolio_id", nullable = false, unique = true)
    private UUID portfolioId;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true)
    private String username;

    @Column(name = "photo_url")
    private String photoUrl;

    private String headline;

    @Column(name = "headline_en")
    private String headlineEn;

    private String bio;

    @Column(name = "bio_en")
    private String bioEn;

    private String location;

    @Column(name = "professional_email")
    private String professionalEmail;

    private String phone;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "website_url")
    private String websiteUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Profile() {
        // JPA
    }

    public Profile(UUID portfolioId) {
        this.portfolioId = portfolioId;
    }

    public void update(String fullName, String username, String headline, String headlineEn, String bio,
            String bioEn, String location, String professionalEmail, String phone, String githubUrl,
            String linkedinUrl, String websiteUrl) {
        this.fullName = fullName;
        this.username = username;
        this.headline = headline;
        this.headlineEn = headlineEn;
        this.bio = bio;
        this.bioEn = bioEn;
        this.location = location;
        this.professionalEmail = professionalEmail;
        this.phone = phone;
        this.githubUrl = githubUrl;
        this.linkedinUrl = linkedinUrl;
        this.websiteUrl = websiteUrl;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getHeadlineEn() {
        return headlineEn;
    }

    public String getBio() {
        return bio;
    }

    public String getBioEn() {
        return bioEn;
    }

    public String getLocation() {
        return location;
    }

    public String getProfessionalEmail() {
        return professionalEmail;
    }

    public String getPhone() {
        return phone;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }
}
