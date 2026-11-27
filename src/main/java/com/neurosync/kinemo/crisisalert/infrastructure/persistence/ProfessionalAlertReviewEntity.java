package com.neurosync.kinemo.crisisalert.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bc03_professional_alert_reviews", indexes = @Index(name = "idx_review_professional", columnList = "professional_id"))
public class ProfessionalAlertReviewEntity {
    @Id @Column(length = 160) private String id;
    @Column(nullable = false) private UUID alertId;
    @Column(name = "professional_id", nullable = false, length = 100) private String professionalId;
    @Column(nullable = false) private Instant reviewedAt;

    protected ProfessionalAlertReviewEntity() {}
    public ProfessionalAlertReviewEntity(UUID alertId, String professionalId) {
        this.id = professionalId + ":" + alertId;
        this.alertId = alertId; this.professionalId = professionalId; this.reviewedAt = Instant.now();
    }
    public UUID getAlertId() { return alertId; }
    public Instant getReviewedAt() { return reviewedAt; }
}
