package com.neurosync.kinemo.child.domain.model.entities;

import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import com.neurosync.kinemo.child.domain.model.valueobjects.InvitationStatus;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CareNetworkInvitation {
    private Long id;
    private String inviteeEmail;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private CareNetworkRole role;
    private PermissionSet permissions;

    public CareNetworkInvitation(String inviteeEmail, CareNetworkRole role, PermissionSet permissions) {
        if (inviteeEmail == null || inviteeEmail.isBlank()) {
            throw new IllegalArgumentException("Invitee email cannot be null or blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (permissions == null) {
            throw new IllegalArgumentException("Permissions cannot be null");
        }

        this.inviteeEmail = inviteeEmail.trim();
        this.role = role;
        this.permissions = permissions;
        this.status = InvitationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusDays(7);
    }

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
