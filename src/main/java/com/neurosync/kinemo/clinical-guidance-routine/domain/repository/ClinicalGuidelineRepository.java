package com.neurosync.kinemo.clinicalguidanceroutine.domain.repository;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.ClinicalGuideline;
import java.util.*;
public interface ClinicalGuidelineRepository {
    Optional<ClinicalGuideline> findById(UUID id);
    java.util.List<ClinicalGuideline> findByChildId(String childId);
    ClinicalGuideline save(ClinicalGuideline value);
}
