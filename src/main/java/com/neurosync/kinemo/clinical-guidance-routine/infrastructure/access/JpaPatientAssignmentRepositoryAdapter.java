package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.PatientAssignmentRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public class JpaPatientAssignmentRepositoryAdapter implements PatientAssignmentRepository {
    private final SpringDataProfessionalAssignmentRepository assignments;
    public JpaPatientAssignmentRepositoryAdapter(SpringDataProfessionalAssignmentRepository assignments){
        this.assignments = assignments;
    }
    public List<String> findActiveChildIdsByProfessionalId(String id){
        return assignments.findByProfessionalIdAndActiveTrue(id).stream()
                .map(ProfessionalAssignmentJpaEntity::getChildId).distinct().sorted().toList();
    }
}
