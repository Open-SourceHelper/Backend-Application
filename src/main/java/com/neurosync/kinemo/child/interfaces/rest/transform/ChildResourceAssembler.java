package com.neurosync.kinemo.child.interfaces.rest.transform;

import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.interfaces.rest.resources.ChildResource;

public final class ChildResourceAssembler {
    private ChildResourceAssembler() {
    }

    public static ChildResource toResource(Child child) {
        return new ChildResource(
                child.getId(),
                child.getParentId(),
                child.getFirstName(),
                child.getLastName(),
                child.getBirthDate()
        );
    }
}
