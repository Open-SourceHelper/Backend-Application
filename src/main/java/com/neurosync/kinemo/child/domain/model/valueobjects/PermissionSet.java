package com.neurosync.kinemo.child.domain.model.valueobjects;

import java.util.HashSet;
import java.util.Set;

public record PermissionSet(Set<String> permissions) {
    public PermissionSet() {
        this(Set.of());
    }

    public PermissionSet {
        if (permissions == null) {
            throw new IllegalArgumentException("Permissions cannot be null");
        }
        permissions = Set.copyOf(permissions);
    }

    public boolean contains(String permission) {
        validatePermission(permission);
        return permissions.contains(permission);
    }

    public PermissionSet add(String permission) {
        validatePermission(permission);

        Set<String> updatedPermissions = new HashSet<>(permissions);
        updatedPermissions.add(permission);
        return new PermissionSet(updatedPermissions);
    }

    public PermissionSet remove(String permission) {
        validatePermission(permission);

        Set<String> updatedPermissions = new HashSet<>(permissions);
        updatedPermissions.remove(permission);
        return new PermissionSet(updatedPermissions);
    }

    private static void validatePermission(String permission) {
        if (permission == null || permission.isBlank()) {
            throw new IllegalArgumentException("Permission cannot be null or blank");
        }
    }
}
