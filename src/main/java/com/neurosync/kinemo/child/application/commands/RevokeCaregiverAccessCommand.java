package com.neurosync.kinemo.child.application.commands;

public record RevokeCaregiverAccessCommand(Long memberId) {
    public RevokeCaregiverAccessCommand {
        if (memberId == null)
            throw new IllegalArgumentException("Member ID cannot be null");
    }
}
