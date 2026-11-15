package com.neurosync.kinemo.child.application.internal.eventhandlers;

import com.neurosync.kinemo.child.domain.model.events.ClinicalProfileUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("childClinicalProfileUpdatedEventHandler")
@Slf4j
public class ClinicalProfileUpdatedEventHandler {
    @EventListener
    public void on(ClinicalProfileUpdated event) {
        log.info(
                "Clinical profile updated: childId={}, occurredAt={}",
                event.getChildId(),
                event.getOccurredAt()
        );
    }
}
