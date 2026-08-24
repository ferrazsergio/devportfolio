package dev.devportfolio.portfolio.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Tenant lógico: raiz de posse de todo o conteúdo do portfólio (ver ADR-002).
 */
@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_user_id", nullable = false, unique = true)
    private UUID ownerUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PortfolioStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Portfolio() {
        // JPA
    }

    public Portfolio(UUID ownerUserId) {
        this.ownerUserId = ownerUserId;
        this.status = PortfolioStatus.DRAFT;
    }

    public void updateStatus(PortfolioStatus status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public PortfolioStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
