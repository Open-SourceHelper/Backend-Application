package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SpringDataProfessionalAssignmentRepository extends JpaRepository<ProfessionalAssignmentJpaEntity,String> {
    boolean existsByChildIdAndProfessionalIdAndActiveTrue(String childId,String professionalId);
    List<ProfessionalAssignmentJpaEntity> findByProfessionalIdAndActiveTrue(String professionalId);
}
