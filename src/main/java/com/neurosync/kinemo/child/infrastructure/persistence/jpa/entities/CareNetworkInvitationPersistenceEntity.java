package com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities;

import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import com.neurosync.kinemo.child.domain.model.valueobjects.InvitationStatus;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.converters.PermissionSetConverter;
import com.neurosync.kinemo.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_network_invitations")
@Getter
@Setter
@NoArgsConstructor
public class CareNetworkInvitationPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "invitee_email", nullable = false)
    private String inviteeEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareNetworkRole role;

    @Convert(converter = PermissionSetConverter.class)
    @Column(columnDefinition = "TEXT", nullable = false)
    private PermissionSet permissions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_network_id", nullable = false)
    private CareNetworkPersistenceEntity careNetwork;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
