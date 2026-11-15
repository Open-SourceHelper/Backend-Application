package com.neurosync.kinemo.child.application.commands;

public record RevokeCaregiverAccessCommand(Long childId, Long memberId) {
    public RevokeCaregiverAccessCommand {
        if (childId == null) {
            throw new IllegalArgumentException("Child ID cannot be null");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }
    }
}
