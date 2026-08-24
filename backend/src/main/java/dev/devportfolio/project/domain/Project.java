package dev.devportfolio.project.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "portfolio_id", nullable = false)
    private UUID portfolioId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "full_description")
    private String fullDescription;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "demo_url")
    private String demoUrl;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    @Column(nullable = false)
    private boolean featured;

    @Column(name = "display_order", nullable = false)
    private int order;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_technology", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill_id")
    private Set<UUID> technologyIds = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Project() {
        // JPA
    }

    public Project(UUID portfolioId, String name, String slug, String shortDescription, String fullDescription,
            String imageUrl, String githubUrl, String demoUrl, LocalDate date, ProjectStatus status,
            boolean featured, int order, Set<UUID> technologyIds) {
        this.portfolioId = portfolioId;
        this.name = name;
        this.slug = slug;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.imageUrl = imageUrl;
        this.githubUrl = githubUrl;
        this.demoUrl = demoUrl;
        this.date = date;
        this.status = status;
        this.featured = featured;
        this.order = order;
        this.technologyIds = new HashSet<>(technologyIds);
    }

    public void update(String name, String slug, String shortDescription, String fullDescription, String imageUrl,
            String githubUrl, String demoUrl, LocalDate date, ProjectStatus status, boolean featured,
            Set<UUID> technologyIds) {
        this.name = name;
        this.slug = slug;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.imageUrl = imageUrl;
        this.githubUrl = githubUrl;
        this.demoUrl = demoUrl;
        this.date = date;
        this.status = status;
        this.featured = featured;
        this.technologyIds.clear();
        this.technologyIds.addAll(technologyIds);
    }

    public void reorder(int order) {
        this.order = order;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public LocalDate getDate() {
        return date;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public boolean isFeatured() {
        return featured;
    }

    public int getOrder() {
        return order;
    }

    public Set<UUID> getTechnologyIds() {
        return technologyIds;
    }
}
