package com.neurosync.kinemo.clinicalguidanceroutine.domain.repository;
import java.util.List;
/** Read-only port for assignments owned by the clinical guidance bounded context. */
public interface PatientAssignmentRepository {
    List<String> findActiveChildIdsByProfessionalId(String professionalId);
}
