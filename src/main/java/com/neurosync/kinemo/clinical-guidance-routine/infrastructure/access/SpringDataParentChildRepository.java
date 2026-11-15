package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SpringDataParentChildRepository extends JpaRepository<ParentChildJpaEntity,String> {
    boolean existsByChildIdAndParentIdAndActiveTrue(String childId,String parentId);
}
