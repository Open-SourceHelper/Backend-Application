package com.neurosync.kinemo.child.domain.model.entities;

import com.neurosync.kinemo.child.domain.model.valueobjects.InvitationStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CareNetworkInvitation {
    private Long id;
    private String inviteeEmail;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }

    public void expire() {
        this.status = InvitationStatus.EXPIRED;
    }

    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiresAt);
    }
}
