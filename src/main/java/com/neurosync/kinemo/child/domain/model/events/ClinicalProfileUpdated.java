package com.neurosync.kinemo.child.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class ClinicalProfileUpdated extends ApplicationEvent {
    private final Long childId;
    private final LocalDateTime occurredAt;

    public ClinicalProfileUpdated(Object source, Long childId, LocalDateTime occurredAt) {
        super(source);
        this.childId = childId;
        this.occurredAt = occurredAt;
    }
}
