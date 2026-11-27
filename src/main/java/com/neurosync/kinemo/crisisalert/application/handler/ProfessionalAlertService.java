package com.neurosync.kinemo.crisisalert.application.handler;

import com.neurosync.kinemo.crisisalert.application.UseCaseException;
import com.neurosync.kinemo.crisisalert.application.port.ProfessionalAlertAccess;
import com.neurosync.kinemo.crisisalert.domain.model.CrisisAlert;
import com.neurosync.kinemo.crisisalert.domain.repository.CrisisAlertRepository;
import com.neurosync.kinemo.crisisalert.infrastructure.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
public class ProfessionalAlertService {
    private final ProfessionalAlertAccess access;
    private final CrisisAlertRepository alerts;
    private final ProfessionalAlertReviewRepository reviews;
    public ProfessionalAlertService(ProfessionalAlertAccess access, CrisisAlertRepository alerts,
                                    ProfessionalAlertReviewRepository reviews) {
        this.access = access; this.alerts = alerts; this.reviews = reviews;
    }
    @Transactional(readOnly = true)
    public List<CrisisAlert> pending(String actor) {
        var acknowledged = new HashSet<>(reviews.findByProfessionalId(actor).stream()
                .map(ProfessionalAlertReviewEntity::getAlertId).toList());
        return access.assignedChildren(actor).stream().distinct()
                .flatMap(child -> alerts.findByChildId(child).stream())
                .filter(alert -> !acknowledged.contains(alert.id()))
                .sorted(Comparator.comparing(CrisisAlert::createdAt)).limit(100).toList();
    }
    @Transactional(readOnly = true)
    public CrisisAlert detail(UUID id, String actor) {
        var alert = alerts.findById(id).orElseThrow(() -> notFound());
        if (!access.canReview(alert.childId(), actor)) throw notFound();
        return alert;
    }
    public Instant acknowledge(UUID id, String actor) {
        detail(id, actor); // Always recheck current assignment, including repeated confirmations.
        var key = actor + ":" + id;
        var existing = reviews.findById(key);
        if (existing.isPresent()) return existing.get().getReviewedAt();
        return reviews.saveAndFlush(new ProfessionalAlertReviewEntity(id, actor)).getReviewedAt();
    }
    private UseCaseException notFound() { return new UseCaseException("ALERT_NOT_FOUND", "La alerta no está disponible para tu cuenta."); }
}
