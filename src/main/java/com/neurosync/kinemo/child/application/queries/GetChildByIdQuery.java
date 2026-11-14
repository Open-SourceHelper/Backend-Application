package com.neurosync.kinemo.child.application.queries;

public record GetChildByIdQuery(Long childId) {
    public GetChildByIdQuery {
        if (childId == null || childId <= 0)
            throw new IllegalArgumentException("Child ID is required.");
    }
}
