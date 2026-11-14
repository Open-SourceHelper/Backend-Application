package com.neurosync.kinemo.child.application.queries;

public record GetCareNetworkByChildIdQuery(Long childId) {
    public GetCareNetworkByChildIdQuery {
        if (childId == null)
            throw new IllegalArgumentException("Child ID cannot be null");
    }
}
