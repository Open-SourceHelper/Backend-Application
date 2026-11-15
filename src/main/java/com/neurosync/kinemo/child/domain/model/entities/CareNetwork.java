package com.neurosync.kinemo.child.domain.model.entities;

import lombok.AccessLevel;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CareNetwork {
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    @Getter(AccessLevel.NONE)
    private final List<CareNetworkInvitation> invitations = new ArrayList<>();

    @Getter(AccessLevel.NONE)
    private final List<CareNetworkMember> members = new ArrayList<>();

    public void addInvitation(CareNetworkInvitation invitation) {
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation cannot be null");
        }
        invitations.add(invitation);
    }

    public List<CareNetworkInvitation> getInvitations() {
        return List.copyOf(invitations);
    }

    public CareNetworkInvitation findInvitationById(Long invitationId) {
        if (invitationId == null) {
            throw new IllegalArgumentException("Invitation ID cannot be null");
        }

        return invitations.stream()
                .filter(invitation -> invitationId.equals(invitation.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invitation not found: " + invitationId));
    }

    public void addMember(CareNetworkMember member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        members.add(member);
    }

    public CareNetworkMember findMemberById(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("Member ID cannot be null");
        }

        return members.stream()
                .filter(member -> memberId.equals(member.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Member not found: " + memberId));
    }

    public List<CareNetworkMember> getMembers() {
        return List.copyOf(members);
    }

    public static CareNetwork reconstitute(Long id, String name, LocalDateTime createdAt) {
        if (name == null || createdAt == null) {
            throw new IllegalArgumentException("Persisted care network name and creation time cannot be null");
        }

        CareNetwork careNetwork = new CareNetwork();
        careNetwork.id = id;
        careNetwork.name = name;
        careNetwork.createdAt = createdAt;
        return careNetwork;
    }
}
