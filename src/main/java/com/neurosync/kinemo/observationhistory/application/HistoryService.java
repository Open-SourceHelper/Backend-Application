package com.neurosync.kinemo.observationhistory.application;

import com.neurosync.kinemo.observationhistory.domain.model.CrisisSeverity;
import com.neurosync.kinemo.observationhistory.domain.model.Episode;
import com.neurosync.kinemo.observationhistory.infrastructure.persistence.EpisodeRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HistoryService {
    public record NewEpisode(String alertId, String description, Instant occurredAt,
                             CrisisSeverity severity, Integer durationMinutes, String trigger, String helped) {}

    private final EpisodeRepository episodes;
    private final HistoryAccess access;
    private final ClosedAlertLookup alerts;

    public HistoryService(EpisodeRepository episodes, ObjectProvider<HistoryAccess> access,
                          ObjectProvider<ClosedAlertLookup> alerts) {
        this.episodes = episodes;
        this.access = access.getIfAvailable(() -> (accountId, childId) -> Optional.empty());
        this.alerts = alerts.getIfAvailable(() -> (alertId, childId) -> false);
    }

    @Transactional(readOnly = true)
    public List<EpisodeDetails> list(String actor, String childId, int page, int size) {
        requireAccess(actor, childId);
        return episodes.findByChildIdOrderByOccurredAtDescIdDesc(childId, PageRequest.of(page, size))
                .stream().map(EpisodeDetails::from).toList();
    }

    @Transactional(readOnly = true)
    public EpisodeDetails get(String actor, String childId, UUID id) {
        requireAccess(actor, childId);
        return EpisodeDetails.from(find(childId, id));
    }

    @Transactional
    public EpisodeDetails register(String actor, String childId, NewEpisode input) {
        if (requireAccess(actor, childId) == HistoryAccess.Role.PSYCHOLOGIST) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only family or an authorized caregiver may register episodes");
        }
        if (input.occurredAt().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Episode date cannot be in the future");
        }
        if (input.alertId() != null) {
            if (!alerts.isClosedForChild(input.alertId(), childId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Alert must be closed and belong to this child");
            }
            if (episodes.existsByAlertId(input.alertId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Alert already has an episode");
            }
        }
        var episode = new Episode(childId, actor, input.alertId(), input.description(), input.occurredAt(),
                input.severity(), input.durationMinutes(), input.trigger(), input.helped());
        return EpisodeDetails.from(episodes.saveAndFlush(episode));
    }

    @Transactional
    public EpisodeDetails comment(String actor, String childId, UUID id, String text) {
        requirePsychologist(actor, childId);
        var episode = find(childId, id);
        episode.addComment(actor, text, Instant.now());
        return EpisodeDetails.from(episode);
    }

    @Transactional
    public EpisodeDetails review(String actor, String childId, UUID id) {
        requirePsychologist(actor, childId);
        var episode = find(childId, id);
        episode.review(actor, Instant.now());
        return EpisodeDetails.from(episode);
    }

    private HistoryAccess.Role requireAccess(String actor, String childId) {
        if (actor == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return access.roleFor(actor, childId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "No history access for this child"));
    }

    private void requirePsychologist(String actor, String childId) {
        if (requireAccess(actor, childId) != HistoryAccess.Role.PSYCHOLOGIST) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Assigned psychologist required");
        }
    }

    private Episode find(String childId, UUID id) {
        return episodes.findByIdAndChildId(id, childId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Episode not found"));
    }
}
