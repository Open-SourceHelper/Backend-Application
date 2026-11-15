package com.neurosync.kinemo.child.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class CaregiverAccessRevoked extends ApplicationEvent {
    private final Long childId;
    private final Long caregiverId;
    private final LocalDateTime occurredAt;

    public CaregiverAccessRevoked(Object source, Long childId, Long caregiverId,
                                  LocalDateTime occurredAt) {
        super(source);
        this.childId = childId;
        this.caregiverId = caregiverId;
        this.occurredAt = occurredAt;
    }
}
