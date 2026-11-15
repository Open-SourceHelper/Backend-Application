package com.neurosync.kinemo.child.application.commands;

import java.time.LocalDate;

public record UpdateChildCommand(Long childId, String firstName, String lastName, LocalDate birthDate) {
    public UpdateChildCommand {
        if (childId == null) {
            throw new IllegalArgumentException("Child ID cannot be null");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name cannot be null or blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name cannot be null or blank");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
    }
}
