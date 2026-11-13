package com.neurosync.kinemo.child.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class ChildRegistered extends ApplicationEvent {
    private final Long childId;
    private final Long parentId;
    private final LocalDateTime occurredAt;

    public ChildRegistered(Object source, Long childId, Long parentId, LocalDateTime occurredAt) {
        super(source);
        this.childId = childId;
        this.parentId = parentId;
        this.occurredAt = occurredAt;
    }
}
