package com.neurosync.kinemo.crisisalert.interfaces.rest;

import com.neurosync.kinemo.crisisalert.application.handler.ProfessionalAlertService;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crisis-alerts/professional-notifications")
public class ProfessionalAlertController {
    private final ProfessionalAlertService service;
    public ProfessionalAlertController(ProfessionalAlertService service) { this.service = service; }
    @GetMapping
    public List<CrisisAlertController.CrisisAlertResponse> pending(Principal principal) {
        return service.pending(principal.getName()).stream().map(CrisisAlertController.CrisisAlertResponse::from).toList();
    }
    @GetMapping("/{id}")
    public CrisisAlertController.CrisisAlertResponse detail(@PathVariable UUID id, Principal principal) {
        return CrisisAlertController.CrisisAlertResponse.from(service.detail(id, principal.getName()));
    }
    public record ReviewResponse(UUID alertId, Instant reviewedAt) {}
    @PostMapping("/{id}/review")
    public ReviewResponse review(@PathVariable UUID id, Principal principal) {
        return new ReviewResponse(id, service.acknowledge(id, principal.getName()));
    }
}
