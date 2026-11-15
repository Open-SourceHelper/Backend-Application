package com.neurosync.kinemo.child.application.internal.eventhandlers;

import com.neurosync.kinemo.child.domain.model.events.ChildRegistered;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("childChildRegisteredEventHandler")
@Slf4j
public class ChildRegisteredEventHandler {
    @EventListener
    public void on(ChildRegistered event) {
        log.info(
                "Caregiver invited: childId={}, parentId={}, occurredAt={}",
                event.getChildId(),
                event.getParentId(),
                event.getOccurredAt()
        );
    }
}
