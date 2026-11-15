package com.neurosync.kinemo.child.interfaces.rest.transform;

import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;
import com.neurosync.kinemo.child.interfaces.rest.resources.CareNetworkResource;

public final class CareNetworkResourceAssembler {
    private CareNetworkResourceAssembler() {
    }

    public static CareNetworkResource toResource(Child child) {
        CareNetwork network = child.getCareNetwork();

        return new CareNetworkResource(
                network.getId(),
                child.getId(),
                network.getMembers(),
                network.getInvitations()
        );
    }
}
