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
        if (!isValid()) {
            throw new IllegalStateException("Invitation is not valid");
        }
        status = InvitationStatus.ACCEPTED;
    }

    public void expire() {
        if (status == InvitationStatus.PENDING) {
            status = InvitationStatus.EXPIRED;
        }
    }

    public boolean isValid() {
        return status == InvitationStatus.PENDING && LocalDateTime.now().isBefore(expiresAt);
    }

    public static CareNetworkInvitation reconstitute(
            Long id,
            String inviteeEmail,
            InvitationStatus status,
            LocalDateTime createdAt,
            LocalDateTime expiresAt,
            CareNetworkRole role,
            PermissionSet permissions
    ) {
        if (status == null || createdAt == null || expiresAt == null) {
            throw new IllegalArgumentException("Persisted invitation status and timestamps cannot be null");
        }

        CareNetworkInvitation invitation =
                new CareNetworkInvitation(inviteeEmail, role, permissions);
        invitation.id = id;
        invitation.status = status;
        invitation.createdAt = createdAt;
        invitation.expiresAt = expiresAt;
        return invitation;
    }
}
