package com.neurosync.kinemo.child.application.commands;

import java.time.LocalDate;

public record CreateChildCommand(Long parentId, String firstName, String lastName, LocalDate birthDate) {
    public CreateChildCommand {
        if (parentId == null) {
            throw new IllegalArgumentException("Parent ID cannot be null");
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
