package com.neurosync.kinemo.child.application.queries;

public record GetCareNetworkMembersQuery(Long childId) {
    public GetCareNetworkMembersQuery {
        if (childId == null) {
            throw new IllegalArgumentException("Child ID cannot be null");
        }
    }
}
