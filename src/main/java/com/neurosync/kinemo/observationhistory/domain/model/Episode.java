package com.neurosync.kinemo.observationhistory.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "history_episodes",
       uniqueConstraints = @UniqueConstraint(name = "uk_history_episode_alert", columnNames = "alert_id"),
       indexes = @Index(name = "ix_history_episode_child_date", columnList = "child_id, occurred_at"))
public class Episode {
    @Id
    private UUID id;
    @Version
    private long version;
    @Column(name = "child_id", nullable = false, length = 128)
    private String childId;
    @Column(nullable = false, length = 128)
    private String authorId;
    @Column(name = "alert_id", length = 128)
    private String alertId;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;
    @Enumerated(EnumType.STRING)
    private CrisisSeverity crisisSeverity;
    private Integer durationMinutes;
    @Column(length = 255)
    private String trigger;
    @Column(length = 255)
    private String helped;
    private Instant reviewedAt;
    @Column(length = 128)
    private String reviewedBy;
    @ElementCollection
    @CollectionTable(name = "history_professional_comments", joinColumns = @JoinColumn(name = "episode_id"))
    @OrderColumn(name = "comment_position")
    private List<ProfessionalComment> comments = new ArrayList<>();

    protected Episode() {}

    public Episode(String childId, String authorId, String alertId, String description,
                   Instant occurredAt, CrisisSeverity crisisSeverity, Integer durationMinutes,
                   String trigger, String helped) {
        if (alertId != null && crisisSeverity == null) {
            throw new IllegalArgumentException("A linked alert requires a crisis severity");
        }
        if (durationMinutes != null && (crisisSeverity == null || durationMinutes < 1)) {
            throw new IllegalArgumentException("Only crisis episodes can have a positive duration");
        }
        this.id = UUID.randomUUID();
        this.childId = childId;
        this.authorId = authorId;
        this.alertId = alertId;
        this.description = description.strip();
        this.occurredAt = occurredAt;
        this.crisisSeverity = crisisSeverity;
        this.durationMinutes = durationMinutes;
        this.trigger = trigger;
        this.helped = helped;
    }

    public void addComment(String authorId, String text, Instant now) {
        comments.add(new ProfessionalComment(UUID.randomUUID(), authorId, text.strip(), now));
    }

    public void review(String authorId, Instant now) {
        if (reviewedAt == null) {
            reviewedBy = authorId;
            reviewedAt = now;
        }
    }

    public UUID getId() { return id; }
    public String getChildId() { return childId; }
    public String getAuthorId() { return authorId; }
    public String getAlertId() { return alertId; }
    public String getDescription() { return description; }
    public Instant getOccurredAt() { return occurredAt; }
    public CrisisSeverity getCrisisSeverity() { return crisisSeverity; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public String getTrigger() { return trigger; }
    public String getHelped() { return helped; }
    public Instant getReviewedAt() { return reviewedAt; }
    public String getReviewedBy() { return reviewedBy; }
    public List<ProfessionalComment> getComments() { return List.copyOf(comments); }
}
