package com.neurosync.kinemo.child.infrastructure.persistence.jpa.assemblers;

import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkInvitation;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkMember;
import com.neurosync.kinemo.child.domain.model.entities.ClinicalProfile;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities.CareNetworkInvitationPersistenceEntity;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities.CareNetworkMemberPersistenceEntity;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities.CareNetworkPersistenceEntity;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities.ChildPersistenceEntity;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities.ClinicalProfilePersistenceEntity;

import java.util.ArrayList;
import java.util.List;

public final class ChildAssembler {

    private ChildAssembler() {
    }

    public static ChildPersistenceEntity toPersistenceFromDomain(Child child) {
        if (child == null) {
            return null;
        }

        ChildPersistenceEntity entity = new ChildPersistenceEntity();
        if (child.getId() != null) {
            entity.setId(child.getId());
        }
        entity.setParentId(child.getParentId());
        entity.setFirstName(child.getFirstName());
        entity.setLastName(child.getLastName());
        entity.setBirthDate(child.getBirthDate());

        entity.setClinicalProfile(toPersistenceFromDomain(child.getClinicalProfile()));
        entity.setCareNetwork(toPersistenceFromDomain(child.getCareNetwork()));

        return entity;
    }

    private static ClinicalProfilePersistenceEntity toPersistenceFromDomain(
            ClinicalProfile clinicalProfile
    ) {
        if (clinicalProfile == null) {
            return null;
        }

        ClinicalProfilePersistenceEntity entity = new ClinicalProfilePersistenceEntity();
        if (clinicalProfile.getId() != null) {
            entity.setId(clinicalProfile.getId());
        }
        entity.setSpecialNeeds(clinicalProfile.getSpecialNeeds());
        entity.setTriggers(clinicalProfile.getTriggers());
        entity.setRegulators(clinicalProfile.getRegulators());
        entity.setUpdateTime(clinicalProfile.getUpdateTime());
        return entity;
    }

    private static CareNetworkPersistenceEntity toPersistenceFromDomain(CareNetwork careNetwork) {
        if (careNetwork == null) {
            return null;
        }

        CareNetworkPersistenceEntity entity = new CareNetworkPersistenceEntity();
        if (careNetwork.getId() != null) {
            entity.setId(careNetwork.getId());
        }
        entity.setName(careNetwork.getName());

        List<CareNetworkInvitationPersistenceEntity> invitations = new ArrayList<>();
        for (CareNetworkInvitation invitation : careNetwork.getInvitations()) {
            CareNetworkInvitationPersistenceEntity invitationEntity =
                    toPersistenceFromDomain(invitation);
            invitationEntity.setCareNetwork(entity);
            invitations.add(invitationEntity);
        }
        entity.setInvitations(invitations);

        List<CareNetworkMemberPersistenceEntity> members = new ArrayList<>();
        for (CareNetworkMember member : careNetwork.getMembers()) {
            CareNetworkMemberPersistenceEntity memberEntity = toPersistenceFromDomain(member);
            memberEntity.setCareNetwork(entity);
            members.add(memberEntity);
        }
        entity.setMembers(members);

        return entity;
    }

    private static CareNetworkInvitationPersistenceEntity toPersistenceFromDomain(
            CareNetworkInvitation invitation
    ) {
        if (invitation == null) {
            return null;
        }

        CareNetworkInvitationPersistenceEntity entity =
                new CareNetworkInvitationPersistenceEntity();
        if (invitation.getId() != null) {
            entity.setId(invitation.getId());
        }
        entity.setInviteeEmail(invitation.getInviteeEmail());
        entity.setStatus(invitation.getStatus());
        entity.setRole(invitation.getRole());
        entity.setPermissions(invitation.getPermissions());
        entity.setExpiresAt(invitation.getExpiresAt());
        return entity;
    }

    private static CareNetworkMemberPersistenceEntity toPersistenceFromDomain(
            CareNetworkMember member
    ) {
        if (member == null) {
            return null;
        }

        CareNetworkMemberPersistenceEntity entity = new CareNetworkMemberPersistenceEntity();
        if (member.getId() != null) {
            entity.setId(member.getId());
        }
        entity.setCaregiverId(member.getCaregiverId());
        entity.setRole(member.getRole());
        entity.setPermissions(member.getPermissions());
        entity.setStatus(member.getStatus());
        entity.setJoinedAt(member.getJoinedAt());
        entity.setRevokedAt(member.getRevokedAt());
        return entity;
    }
}
