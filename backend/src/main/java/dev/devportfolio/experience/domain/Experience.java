package dev.devportfolio.experience.domain;

import dev.devportfolio.shared.domain.DomainValidationException;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
@Table(name = "experiences")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "portfolio_id", nullable = false)
    private UUID portfolioId;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean current;

    private String location;

    @Column(name = "display_order", nullable = false)
    private int order;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "experience_technology", joinColumns = @JoinColumn(name = "experience_id"))
    @Column(name = "skill_id")
    private Set<UUID> technologyIds = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Experience() {
        // JPA
    }

    public Experience(UUID portfolioId, String company, String role, String description, LocalDate startDate,
            LocalDate endDate, boolean current, String location, int order, Set<UUID> technologyIds) {
        validate(startDate, endDate, current);
        this.portfolioId = portfolioId;
        this.company = company;
        this.role = role;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.location = location;
        this.order = order;
        this.technologyIds = new HashSet<>(technologyIds);
    }

    public void update(String company, String role, String description, LocalDate startDate, LocalDate endDate,
            boolean current, String location, Set<UUID> technologyIds) {
        validate(startDate, endDate, current);
        this.company = company;
        this.role = role;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.current = current;
        this.location = location;
        this.technologyIds.clear();
        this.technologyIds.addAll(technologyIds);
    }

    public void reorder(int order) {
        this.order = order;
    }

    private static void validate(LocalDate startDate, LocalDate endDate, boolean current) {
        if (current && endDate != null) {
            throw new DomainValidationException("Uma experiência marcada como atual não pode ter data de término.");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new DomainValidationException("A data de término não pode ser anterior à data de início.");
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getPortfolioId() {
        return portfolioId;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isCurrent() {
        return current;
    }

    public String getLocation() {
        return location;
    }

    public int getOrder() {
        return order;
    }

    public Set<UUID> getTechnologyIds() {
        return technologyIds;
    }
}
