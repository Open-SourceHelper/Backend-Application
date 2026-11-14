package com.neurosync.kinemo.child.application.commands;

public record AcceptCareInvitationCommand(Long invitationId) {
    public AcceptCareInvitationCommand {
        if (invitationId == null)
            throw new IllegalArgumentException("Invitation ID cannot be null");
    }
}
