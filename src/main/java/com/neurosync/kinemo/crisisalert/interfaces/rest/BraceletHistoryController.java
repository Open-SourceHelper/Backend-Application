package com.neurosync.kinemo.crisisalert.interfaces.rest;

import com.neurosync.kinemo.crisisalert.application.handler.BraceletHistoryService;
import com.neurosync.kinemo.crisisalert.domain.model.BraceletEventType;
import com.neurosync.kinemo.crisisalert.infrastructure.persistence.BraceletEventEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crisis-alerts/bracelet-history")
public class BraceletHistoryController {
    private final BraceletHistoryService service;
    public BraceletHistoryController(BraceletHistoryService service) { this.service = service; }
    public record EventRequest(@NotNull UUID eventId, @NotBlank @Size(max = 80) String childId,
                               @NotNull BraceletEventType type) {}
    @PostMapping
    public BraceletEventEntity.View record(@Valid @RequestBody EventRequest input, Principal principal) {
        return service.record(input.eventId(), input.childId(), input.type(), principal.getName());
    }
    @GetMapping
    public List<BraceletEventEntity.View> list(@RequestParam String childId, Principal principal) {
        return service.history(childId, principal.getName());
    }
}
