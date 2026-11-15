package com.neurosync.kinemo.child.application.internal.queryservices;

import com.neurosync.kinemo.child.application.queries.GetCareInvitationsQuery;
import com.neurosync.kinemo.child.application.queries.GetCareNetworkByChildIdQuery;
import com.neurosync.kinemo.child.application.queries.GetCareNetworkMembersQuery;
import com.neurosync.kinemo.child.application.queryservices.CareNetworkQueryService;
import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkInvitation;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkMember;
import com.neurosync.kinemo.child.domain.repositories.ChildRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CareNetworkQueryServiceImpl implements CareNetworkQueryService {
    private final ChildRepository childRepository;

    public CareNetworkQueryServiceImpl(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    @Override
    public Optional<CareNetwork> handle(GetCareNetworkByChildIdQuery query) {
        return childRepository.findById(query.childId())
                .map(child -> child.getCareNetwork());
    }

    @Override
    public List<CareNetworkMember> handle(GetCareNetworkMembersQuery query) {
        return findChild(query.childId()).getCareNetwork().getMembers();
    }

    @Override
    public List<CareNetworkInvitation> handle(GetCareInvitationsQuery query) {
        return findChild(query.childId()).getCareNetwork().getInvitations();
    }

    private Child findChild(Long childId) {
        return childRepository.findById(childId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Child or care resource not found"
                ));
    }
}
