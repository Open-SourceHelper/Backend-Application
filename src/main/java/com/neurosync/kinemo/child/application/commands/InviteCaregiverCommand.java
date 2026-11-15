package com.neurosync.kinemo.child.application.commands;

import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;

import java.util.List;

public record InviteCaregiverCommand(Long childId, String inviteeEmail, CareNetworkRole role,
                                     List<String> permissions) {
    public InviteCaregiverCommand {
        if (childId == null) {
            throw new IllegalArgumentException("Child ID cannot be null");
        }
        if (inviteeEmail == null || inviteeEmail.isBlank()) {
            throw new IllegalArgumentException("Invitee email cannot be null or blank");
        }
        inviteeEmail = inviteeEmail.trim();

        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        if (permissions == null) {
            throw new IllegalArgumentException("Permissions cannot be null");
        }
        if (permissions.stream().anyMatch(permission -> permission == null || permission.isBlank())) {
            throw new IllegalArgumentException("Permissions cannot contain null or blank values");
        }

        permissions = List.copyOf(permissions);
    }
}
