package com.neurosync.kinemo.child.application.internal.eventhandlers;

import com.neurosync.kinemo.child.domain.model.events.CaregiverAccessRevoked;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("childCaregiverAccessRevokedEventHandler")
@Slf4j
public class CaregiverAccessRevokedEventHandler {

    @EventListener
    public void on(CaregiverAccessRevoked event) {
        log.info(
                "Caregiver access revoked: childId={}, caregiverId={}, occurredAt={}",
                event.getChildId(),
                event.getCaregiverId(),
                event.getOccurredAt()
        );
    }
}
