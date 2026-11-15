package com.neurosync.kinemo.child.application.commands;

public record AcceptCareInvitationCommand(
        Long childId,
        Long invitationId,
        Long caregiverId,
        String verifiedEmail
) {
    public AcceptCareInvitationCommand {
        if (childId == null) {
            throw new IllegalArgumentException("Child ID cannot be null");
        }
        if (invitationId == null) {
            throw new IllegalArgumentException("Invitation ID cannot be null");
        }
        if (caregiverId == null) {
            throw new IllegalArgumentException("Caregiver ID cannot be null");
        }
        if (verifiedEmail == null || verifiedEmail.isBlank()) {
            throw new IllegalArgumentException("Verified email cannot be null or blank");
        }
        verifiedEmail = verifiedEmail.trim();
    }
}
