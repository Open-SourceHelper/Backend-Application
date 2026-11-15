package com.neurosync.kinemo.observationhistory.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.Instant;
import java.util.UUID;

@Embeddable
public record ProfessionalComment(
        @Column(nullable = false) UUID id,
        @Column(nullable = false, length = 128) String authorId,
        @Column(nullable = false, length = 1000) String text,
        @Column(nullable = false) Instant createdAt) {
}
