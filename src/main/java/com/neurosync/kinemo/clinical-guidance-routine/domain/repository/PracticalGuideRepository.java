package com.neurosync.kinemo.clinicalguidanceroutine.domain.repository;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.PracticalGuide;
import java.util.*;
public interface PracticalGuideRepository {
    Optional<PracticalGuide> findById(UUID id);
    java.util.List<PracticalGuide> findPublished();

}
