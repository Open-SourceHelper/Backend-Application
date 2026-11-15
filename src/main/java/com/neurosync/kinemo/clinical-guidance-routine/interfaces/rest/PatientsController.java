package com.neurosync.kinemo.clinicalguidanceroutine.interfaces.rest;
import com.neurosync.kinemo.clinicalguidanceroutine.application.handler.PatientAssignmentQueryHandler;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
/** Exposes only child identifiers assigned to the authenticated professional (US26). */
@RestController
@RequestMapping("/api/v1/patients")
public class PatientsController {
    private final PatientAssignmentQueryHandler queries;
    public PatientsController(PatientAssignmentQueryHandler queries){ this.queries = queries; }
    public record AssignedPatientResponse(String childId) {}
    @GetMapping
    public List<AssignedPatientResponse> mine(Authentication authentication){
        return queries.forProfessional(ActorFromAuthentication.from(authentication)).stream()
                .map(AssignedPatientResponse::new).toList();
    }
}
