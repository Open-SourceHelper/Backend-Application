package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface SpringDataRoutineRepository extends JpaRepository<RoutineJpaEntity,UUID> {
    java.util.List<RoutineJpaEntity> findByChildId(String childId);
}
