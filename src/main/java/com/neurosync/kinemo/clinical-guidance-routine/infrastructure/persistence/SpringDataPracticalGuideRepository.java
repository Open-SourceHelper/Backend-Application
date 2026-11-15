package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataPracticalGuideRepository extends JpaRepository<PracticalGuideJpaEntity,UUID> {
    java.util.List<PracticalGuideJpaEntity> findByPublishedTrue();
}
