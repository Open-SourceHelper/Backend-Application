package com.neurosync.kinemo.child.domain.model.entities;

import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import com.neurosync.kinemo.child.domain.model.valueobjects.MemberStatus;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;

import java.time.LocalDateTime;

public class CareNetworkMember {
    private Long id;
    private Long caregiverId;
    private CareNetworkRole role;
    private PermissionSet permissions;
    private MemberStatus status;
    private LocalDateTime joinedAt;
    private LocalDateTime revokedAt;

    private void validatePermissions(PermissionSet permissions) {
        if (permissions == null) {
            throw new IllegalArgumentException("Permissions cannot be null");
        }
    }

    public void assignPermissions(PermissionSet permissions) {
        validatePermissions(permissions);

        if (this.permissions != null) {
            throw new IllegalStateException("Permissions have already been assigned");
        }

        this.permissions = permissions;
    }

    public void updatePermissions(PermissionSet permissions) {
        validatePermissions(permissions);

        if (this.permissions == null) {
            throw new IllegalStateException("Permissions have not been assigned yet");
        }

        this.permissions = permissions;
    }

    public void revokeAccess() {
        if (this.status != MemberStatus.REVOKED) {
            this.status = MemberStatus.REVOKED;
            this.revokedAt = LocalDateTime.now();
        }
    }

    public boolean hasAccess() {
        return this.status == MemberStatus.ACTIVE;
    }
}
