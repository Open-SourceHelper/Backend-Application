package com.neurosync.kinemo.crisisalert.infrastructure.persistence;

import com.neurosync.kinemo.crisisalert.domain.model.BraceletEventType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bc03_bracelet_events", indexes = @Index(name = "idx_bracelet_child_time", columnList = "child_id,occurred_at"))
public class BraceletEventEntity {
    @Id private UUID id;
    @Column(name = "child_id", nullable = false, length = 80) private String childId;
    @Column(nullable = false, length = 100) private String actorId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private BraceletEventType type;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;

    protected BraceletEventEntity() {}
    public BraceletEventEntity(UUID id, String childId, String actorId, BraceletEventType type) {
        this.id = id; this.childId = childId; this.actorId = actorId; this.type = type; this.occurredAt = Instant.now();
    }
    public record View(UUID id, String childId, String actorId, BraceletEventType type, Instant occurredAt) {}
    public View view() { return new View(id, childId, actorId, type, occurredAt); }
}
