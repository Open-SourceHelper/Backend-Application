package com.neurosync.kinemo.child.application.queryservices;

import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;

import java.util.List;
import java.util.Optional;

public interface CareNetworkQueryService {
    Optional<CareNetwork> getCareNetworkByChildIdQuery(Long childId);
    List<CareNetwork> getCareNetworkMembersQuery();
    List<CareNetwork> getCareInvitationsQuery();
}
