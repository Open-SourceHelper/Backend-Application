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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ChildPersistenceAssembler {

    private ChildPersistenceAssembler() {
    }

    public static Child toDomainFromPersistence(ChildPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity.getClinicalProfile() == null || entity.getCareNetwork() == null) {
            throw new IllegalArgumentException(
                    "Persisted child must have a clinical profile and care network");
        }

        var profileEntity = entity.getClinicalProfile();
        var clinicalProfile = ClinicalProfile.reconstitute(
                profileEntity.getId(),
                profileEntity.getSpecialNeeds(),
                profileEntity.getTriggers(),
                profileEntity.getRegulators(),
                profileEntity.getUpdateTime()
        );

        var networkEntity = entity.getCareNetwork();
        var careNetwork = CareNetwork.reconstitute(
                networkEntity.getId(),
                networkEntity.getName(),
                toLocalDateTime(networkEntity.getCreatedAt())
        );

        if (networkEntity.getInvitations() != null) {
            for (var invitation : networkEntity.getInvitations()) {
                careNetwork.addInvitation(CareNetworkInvitation.reconstitute(
                        invitation.getId(),
                        invitation.getInviteeEmail(),
                        invitation.getStatus(),
                        toLocalDateTime(invitation.getCreatedAt()),
                        invitation.getExpiresAt(),
                        invitation.getRole(),
                        invitation.getPermissions()
                ));
            }
        }

        if (networkEntity.getMembers() != null) {
            for (var member : networkEntity.getMembers()) {
                careNetwork.addMember(CareNetworkMember.reconstitute(
                        member.getId(),
                        member.getCaregiverId(),
                        member.getRole(),
                        member.getPermissions(),
                        member.getStatus(),
                        member.getJoinedAt(),
                        member.getRevokedAt()
                ));
            }
        }

        return Child.reconstitute(
                entity.getId(),
                entity.getParentId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                toLocalDateTime(entity.getCreatedAt()),
                clinicalProfile,
                careNetwork
        );
    }

    private static LocalDateTime toLocalDateTime(Date value) {
        if (value == null) {
            throw new IllegalArgumentException("Audit timestamp cannot be null");
        }
        return value.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static ChildPersistenceEntity toPersistenceFromDomain(Child child) {
        if (child == null) return null;
        var entity = new ChildPersistenceEntity();

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
            ClinicalProfile profile
    ) {
        if (profile == null) {
            return null;
        }

        var entity = new ClinicalProfilePersistenceEntity();
        if (profile.getId() != null) {
            entity.setId(profile.getId());
        }
        entity.setSpecialNeeds(profile.getSpecialNeeds());
        entity.setTriggers(profile.getTriggers());
        entity.setRegulators(profile.getRegulators());
        entity.setUpdateTime(profile.getUpdateTime());

        return entity;
    }

    private static CareNetworkPersistenceEntity toPersistenceFromDomain(CareNetwork careNetwork) {
        if (careNetwork == null) {
            return null;
        }

        var entity = new CareNetworkPersistenceEntity();
        if (careNetwork.getId() != null) {
            entity.setId(careNetwork.getId());
        }
        entity.setName(careNetwork.getName());

        List<CareNetworkInvitationPersistenceEntity> invitations = new ArrayList<>();
        for (var invitation : careNetwork.getInvitations()) {
            var invitationEntity = toPersistenceFromDomain(invitation);
            invitationEntity.setCareNetwork(entity);
            invitations.add(invitationEntity);
        }
        entity.setInvitations(invitations);

        List<CareNetworkMemberPersistenceEntity> members = new ArrayList<>();
        for (var member : careNetwork.getMembers()) {
            var memberEntity = toPersistenceFromDomain(member);
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

        var entity = new CareNetworkInvitationPersistenceEntity();
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

        var entity = new CareNetworkMemberPersistenceEntity();
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
