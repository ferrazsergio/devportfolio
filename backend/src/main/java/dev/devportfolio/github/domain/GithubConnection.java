package dev.devportfolio.github.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "github_connections")
public class GithubConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "portfolio_id", nullable = false)
    private UUID portfolioId;

    @Column(name = "github_username", nullable = false)
    private String githubUsername;

    @Column(name = "encrypted_access_token", nullable = false)
    private String encryptedAccessToken;

    @CreationTimestamp
    @Column(name = "connected_at", nullable = false, updatable = false)
    private Instant connectedAt;

    protected GithubConnection() {
        // JPA
    }

    public GithubConnection(UUID portfolioId, String githubUsername, String encryptedAccessToken) {
        this.portfolioId = portfolioId;
        this.githubUsername = githubUsername;
        this.encryptedAccessToken = encryptedAccessToken;
    }

    public void updateToken(String githubUsername, String encryptedAccessToken) {
        this.githubUsername = githubUsername;
        this.encryptedAccessToken = encryptedAccessToken;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public String getEncryptedAccessToken() {
        return encryptedAccessToken;
    }

    public Instant getConnectedAt() {
        return connectedAt;
    }
}
