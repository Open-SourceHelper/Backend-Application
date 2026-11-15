package com.neurosync.kinemo.observationhistory.application;

import com.neurosync.kinemo.observationhistory.domain.model.Episode;
import com.neurosync.kinemo.observationhistory.domain.model.ProfessionalComment;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public record EpisodeDetails(UUID id, String childId, String caregiverId, String alertId,
                             String description, Instant occurredAt, String crisisSeverity,
                             Integer durationMinutes, String trigger, String helped,
                             Instant reviewedAt, String reviewedBy, List<ProfessionalComment> comments) {
    static EpisodeDetails from(Episode episode) {
        var severity = episode.getCrisisSeverity();
        return new EpisodeDetails(
                episode.getId(), episode.getChildId(), episode.getAuthorId(), episode.getAlertId(),
                episode.getDescription(), episode.getOccurredAt(),
                severity == null ? null : severity.name().toLowerCase(Locale.ROOT),
                episode.getDurationMinutes(), episode.getTrigger(), episode.getHelped(),
                episode.getReviewedAt(), episode.getReviewedBy(), episode.getComments());
    }
}
