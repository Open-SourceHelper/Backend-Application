package com.neurosync.kinemo.child.application.queries;

public record GetClinicalProfileByChildIdQuery(Long childId) {
    public GetClinicalProfileByChildIdQuery {
        if (childId == null)
            throw new IllegalArgumentException("Child ID cannot be null");
    }
}
