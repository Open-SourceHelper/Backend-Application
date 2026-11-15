package com.neurosync.kinemo.child.application.queryservices;

import com.neurosync.kinemo.child.application.queries.GetCareInvitationsQuery;
import com.neurosync.kinemo.child.application.queries.GetCareNetworkByChildIdQuery;
import com.neurosync.kinemo.child.application.queries.GetCareNetworkMembersQuery;
import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkInvitation;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkMember;

import java.util.List;
import java.util.Optional;

public interface CareNetworkQueryService {
    Optional<CareNetwork> handle(GetCareNetworkByChildIdQuery query);
    List<CareNetworkMember> handle(GetCareNetworkMembersQuery query);
    List<CareNetworkInvitation> handle(GetCareInvitationsQuery query);
}
