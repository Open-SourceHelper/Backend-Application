package com.neurosync.kinemo.child.domain.model.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class CaregiverInvited extends ApplicationEvent {
    private final Long childId;
    private final String inviteeEmail;
    private final LocalDateTime occurredAt;

    public CaregiverInvited(Object source, Long childId, String inviteeEmail,
                            LocalDateTime occurredAt) {
        super(source);
        this.childId = childId;
        this.inviteeEmail = inviteeEmail;
        this.occurredAt = occurredAt;
    }
}
