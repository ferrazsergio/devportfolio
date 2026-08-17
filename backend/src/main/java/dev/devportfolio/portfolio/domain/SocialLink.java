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

@Entity
@Table(name = "social_links")
public class SocialLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "portfolio_id", nullable = false)
    private UUID portfolioId;

    @Column(nullable = false)
    private String platform;

    @Column(nullable = false)
    private String url;

    @Column(name = "display_order", nullable = false)
    private int order;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SocialLink() {
        // JPA
    }

    public SocialLink(UUID portfolioId, String platform, String url, int order) {
        this.portfolioId = portfolioId;
        this.platform = platform;
        this.url = url;
        this.order = order;
    }

    public void update(String platform, String url, int order) {
        this.platform = platform;
        this.url = url;
        this.order = order;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public String getPlatform() {
        return platform;
    }

    public String getUrl() {
        return url;
    }

    public int getOrder() {
        return order;
    }
}
