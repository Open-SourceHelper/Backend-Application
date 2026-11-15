package com.neurosync.kinemo.child.application.queries;

public record GetCareInvitationsQuery(Long childId) {
    public GetCareInvitationsQuery {
        if (childId == null) {
            throw new IllegalArgumentException("Child ID cannot be null");
        }
    }
}
