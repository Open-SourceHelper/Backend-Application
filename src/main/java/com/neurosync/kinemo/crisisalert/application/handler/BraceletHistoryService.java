package com.neurosync.kinemo.crisisalert.application.handler;

import com.neurosync.kinemo.crisisalert.application.UseCaseException;
import com.neurosync.kinemo.crisisalert.application.port.CareNetworkAuthorizationPort;
import com.neurosync.kinemo.crisisalert.application.port.ProfessionalAlertAccess;
import com.neurosync.kinemo.crisisalert.domain.model.BraceletEventType;
import com.neurosync.kinemo.crisisalert.infrastructure.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BraceletHistoryService {
    private final BraceletEventRepository events;
    private final CareNetworkAuthorizationPort care;
    private final ProfessionalAlertAccess professionals;
    public BraceletHistoryService(BraceletEventRepository events, CareNetworkAuthorizationPort care,
                                  ProfessionalAlertAccess professionals) {
        this.events = events; this.care = care; this.professionals = professionals;
    }
    public BraceletEventEntity.View record(UUID eventId, String childId, BraceletEventType type, String actor) {
        String child = clean(childId);
        if (!care.findAuthorizedCaregiverIds(child).contains(actor)) denied();
        var existing = events.findById(eventId);
        if (existing.isPresent()) {
            var view = existing.get().view();
            if (!view.childId().equals(child) || !view.actorId().equals(actor) || view.type() != type)
                throw new UseCaseException("INVALID_REQUEST", "El identificador del evento ya se utilizó.");
            return view;
        }
        return events.saveAndFlush(new BraceletEventEntity(eventId, child, actor, type)).view();
    }
    @Transactional(readOnly = true)
    public List<BraceletEventEntity.View> history(String childId, String actor) {
        String child = clean(childId);
        if (!care.findAuthorizedCaregiverIds(child).contains(actor) && !professionals.canReview(child, actor)) denied();
        return events.findTop100ByChildIdOrderByOccurredAtDesc(child).stream().map(BraceletEventEntity::view).toList();
    }
    private String clean(String childId) {
        if (childId == null || childId.isBlank() || childId.length() > 80)
            throw new UseCaseException("INVALID_REQUEST", "El perfil del niño es obligatorio.");
        return childId.trim();
    }
    private void denied() { throw new UseCaseException("ACCESS_DENIED", "No tienes acceso a la pulsera de este niño."); }
}
