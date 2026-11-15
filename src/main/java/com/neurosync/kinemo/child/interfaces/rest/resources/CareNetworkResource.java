package com.neurosync.kinemo.child.interfaces.rest.resources;

import com.neurosync.kinemo.child.domain.model.entities.CareNetworkInvitation;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkMember;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "CareNetworkResponse", description = "Care network resource")
public record CareNetworkResource(
        @Schema(example = "456")
        Long id,

        @Schema(example = "123")
        Long childId,

        @Schema(description = "Care network members")
        List<CareNetworkMember> members,

        @Schema(description = "Care network invitations")
        List<CareNetworkInvitation> invitations
) {
    public CareNetworkResource {
        members = List.copyOf(members);
        invitations = List.copyOf(invitations);
    }
}
