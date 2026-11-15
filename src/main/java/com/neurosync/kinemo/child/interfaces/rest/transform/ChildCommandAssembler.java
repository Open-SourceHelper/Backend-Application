package com.neurosync.kinemo.child.interfaces.rest.transform;

import com.neurosync.kinemo.child.application.commands.CreateChildCommand;
import com.neurosync.kinemo.child.application.commands.UpdateChildCommand;
import com.neurosync.kinemo.child.application.commands.UpdateClinicalProfileCommand;
import com.neurosync.kinemo.child.interfaces.rest.resources.CreateChildResource;
import com.neurosync.kinemo.child.interfaces.rest.resources.UpdateChildResource;
import com.neurosync.kinemo.child.interfaces.rest.resources.UpdateClinicalProfileResource;

public final class ChildCommandAssembler {
    private ChildCommandAssembler() {
    }

    public static CreateChildCommand toCommand(
            CreateChildResource resource,
            Long authenticatedParentId
    ) {
        if (resource == null || authenticatedParentId == null) {
            throw new IllegalArgumentException("Resource and authenticated parent ID are required");
        }
        if (!authenticatedParentId.equals(resource.parentId())) {
            throw new IllegalArgumentException("Parent ID must match the authenticated account");
        }

        return new CreateChildCommand(
                authenticatedParentId,
                resource.firstName(),
                resource.lastName(),
                resource.birthDate()
        );
    }

    public static UpdateChildCommand toCommand(UpdateChildResource resource, Long childId) {
        return new UpdateChildCommand(
                childId,
                resource.firstName(),
                resource.lastName(),
                resource.birthDate()
        );
    }

    public static UpdateClinicalProfileCommand toCommand(
            UpdateClinicalProfileResource resource,
            Long childId
    ) {
        return new UpdateClinicalProfileCommand(
                childId,
                resource.specialNeeds(),
                resource.triggers(),
                resource.regulators()
        );
    }
}
