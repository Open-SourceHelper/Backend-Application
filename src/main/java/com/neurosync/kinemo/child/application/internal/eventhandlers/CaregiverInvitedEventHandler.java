package com.neurosync.kinemo.child.application.internal.eventhandlers;

import com.neurosync.kinemo.child.domain.model.events.CaregiverInvited;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("childCaregiverInvitedEventHandler")
@Slf4j
public class CaregiverInvitedEventHandler {
    @EventListener
    public void on(CaregiverInvited event) {
        log.info(
                "Caregiver invited: childId={}, inviteeEmail={}, occurredAt={}",
                event.getChildId(),
                event.getInviteeEmail(),
                event.getOccurredAt()
        );
    }
}
