package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataClinicalGuidelineRepository extends JpaRepository<ClinicalGuidelineJpaEntity,UUID> {
    java.util.List<ClinicalGuidelineJpaEntity> findByChildId(String childId);
}
