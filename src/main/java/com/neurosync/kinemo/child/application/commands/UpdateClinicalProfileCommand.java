package com.neurosync.kinemo.child.application.commands;

public record UpdateClinicalProfileCommand(Long childId, String specialNeeds, String triggers,
                                           String regulators) {
    public UpdateClinicalProfileCommand {
        if (childId == null) {
            throw new IllegalArgumentException("Child ID cannot be null");
        }
    }
}
