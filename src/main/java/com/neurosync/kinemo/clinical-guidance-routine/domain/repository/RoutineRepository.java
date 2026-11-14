package com.neurosync.kinemo.clinicalguidanceroutine.domain.repository;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.Routine;
import java.util.*;
public interface RoutineRepository {
    Optional<Routine> findById(UUID id);
    java.util.List<Routine> findByChildId(String childId);
    Routine save(Routine value);
}
