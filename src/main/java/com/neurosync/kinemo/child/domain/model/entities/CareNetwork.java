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

    public void addInvitation(CareNetworkInvitation invitation) {
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation cannot be null");
        }
        invitations.add(invitation);
    }

    public List<CareNetworkInvitation> getInvitations() {
        return List.copyOf(invitations);
    }
}
