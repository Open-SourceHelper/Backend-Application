package com.neurosync.kinemo.child.application.commands;

import java.util.List;

public record UpdateMemberPermissionsCommand(Long memberId, List<String> permissions) {
    public UpdateMemberPermissionsCommand {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
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
