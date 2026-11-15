package com.neurosync.kinemo.clinicalguidanceroutine.application.handler;
import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.PatientAssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
public class PatientAssignmentQueryHandler {
    private final PatientAssignmentRepository assignments;
    public PatientAssignmentQueryHandler(PatientAssignmentRepository assignments){ this.assignments = assignments; }
    @Transactional(readOnly=true)
    public List<String> forProfessional(Actor actor){
        if (!actor.hasRole("PSYCHOLOGIST"))
            throw new UseCaseException("ACCESS_DENIED", "Solo el profesional puede consultar sus pacientes.");
        return assignments.findActiveChildIdsByProfessionalId(actor.id());
    }
}
