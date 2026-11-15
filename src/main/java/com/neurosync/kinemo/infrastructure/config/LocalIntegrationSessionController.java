package com.neurosync.kinemo.infrastructure.config;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/local-demo")
@Profile("local")
class LocalIntegrationSessionController {
    private final LocalIntegrationDemo demo;

    LocalIntegrationSessionController(LocalIntegrationDemo demo) {
        this.demo = demo;
    }

    @GetMapping("/session")
    ResponseEntity<Session> session(Principal principal) {
        String role = switch (principal.getName()) {
            case "7001" -> "FAMILY";
            case "7002" -> "CAREGIVER";
            case "7003" -> "PSYCHOLOGIST";
            default -> null;
        };
        return role == null ? ResponseEntity.status(403).build()
                : ResponseEntity.ok(new Session(role, demo.childId()));
    }

    record Session(String role, Long childId) {}
}
