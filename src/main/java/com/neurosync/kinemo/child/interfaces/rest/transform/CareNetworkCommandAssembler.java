package com.neurosync.kinemo.child.interfaces.rest.transform;

import com.neurosync.kinemo.child.application.commands.InviteCaregiverCommand;
import com.neurosync.kinemo.child.application.commands.UpdateMemberPermissionsCommand;
import com.neurosync.kinemo.child.interfaces.rest.resources.InviteCaregiverResource;
import com.neurosync.kinemo.child.interfaces.rest.resources.UpdatePermissionsResource;

public final class CareNetworkCommandAssembler {
    private CareNetworkCommandAssembler() {
    }

    public static InviteCaregiverCommand toCommand(
            InviteCaregiverResource resource,
            Long childId
    ) {
        return new InviteCaregiverCommand(
                childId,
                resource.inviteeEmail(),
                resource.role(),
                resource.permissions()
        );
    }

    public static UpdateMemberPermissionsCommand toCommand(
            UpdatePermissionsResource resource,
            Long childId,
            Long memberId
    ) {
        return new UpdateMemberPermissionsCommand(
                childId,
                memberId,
                resource.permissions()
        );
    }
}
