package com.neurosync.kinemo.crisisalert.application.handler;

import com.neurosync.kinemo.crisisalert.application.UseCaseException;
import com.neurosync.kinemo.crisisalert.application.port.CareNetworkAuthorizationPort;
import com.neurosync.kinemo.crisisalert.application.query.AlertsForChildQuery;
import com.neurosync.kinemo.crisisalert.domain.model.AlertNotification;
import com.neurosync.kinemo.crisisalert.domain.model.CrisisAlert;
import com.neurosync.kinemo.crisisalert.domain.repository.AlertNotificationRepository;
import com.neurosync.kinemo.crisisalert.domain.repository.CrisisAlertRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AlertQueryHandler {
    private final CrisisAlertRepository alerts;
    private final AlertNotificationRepository notifications;
    private final CareNetworkAuthorizationPort careNetwork;

    public AlertQueryHandler(CrisisAlertRepository alerts,
                             AlertNotificationRepository notifications,
                             CareNetworkAuthorizationPort careNetwork) {
        this.alerts = alerts;
        this.notifications = notifications;
        this.careNetwork = careNetwork;
    }

    @Transactional(readOnly = true)
    public List<CrisisAlert> handle(AlertsForChildQuery query, String actorId) {
        if (query.childId() == null || query.childId().isBlank()) {
            throw new UseCaseException("INVALID_REQUEST", "El niño es obligatorio.");
        }
        String childId = query.childId().trim();
        if (!careNetwork.findAuthorizedCaregiverIds(childId).contains(actorId)) {
            throw new UseCaseException("ACCESS_DENIED",
                    "No tienes acceso a las alertas de este niño.");
        }
        return alerts.findByChildId(childId);
    }

    @Transactional(readOnly = true)
    public CrisisAlert find(UUID alertId, String actorId) {
        var alert = alerts.findById(alertId)
                .orElseThrow(() -> new UseCaseException("ALERT_NOT_FOUND", "No se encontró la alerta."));
        if (!careNetwork.findAuthorizedCaregiverIds(alert.childId()).contains(actorId)) {
            throw new UseCaseException("ALERT_NOT_FOUND", "No se encontró la alerta.");
        }
        return alert;
    }

    @Transactional(readOnly = true)
    public List<AlertNotification> inbox(String actorId) {
        return notifications.findByRecipient(actorId).stream()
                .filter(notice -> careNetwork.findAuthorizedCaregiverIds(notice.childId()).contains(actorId))
                .toList();
    }
}
