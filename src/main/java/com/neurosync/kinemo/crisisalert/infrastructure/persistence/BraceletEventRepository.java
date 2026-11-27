package com.neurosync.kinemo.crisisalert.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BraceletEventRepository extends JpaRepository<BraceletEventEntity, UUID> {
    List<BraceletEventEntity> findTop100ByChildIdOrderByOccurredAtDesc(String childId);
}
