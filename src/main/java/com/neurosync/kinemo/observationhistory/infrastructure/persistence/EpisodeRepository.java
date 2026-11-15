package com.neurosync.kinemo.observationhistory.infrastructure.persistence;

import com.neurosync.kinemo.observationhistory.domain.model.Episode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EpisodeRepository extends JpaRepository<Episode, UUID> {
    List<Episode> findByChildIdOrderByOccurredAtDescIdDesc(String childId, Pageable page);

    Optional<Episode> findByIdAndChildId(UUID id, String childId);

    boolean existsByAlertId(String alertId);
}
