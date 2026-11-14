package com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities;

import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import com.neurosync.kinemo.child.domain.model.valueobjects.MemberStatus;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.converters.PermissionSetConverter;
import com.neurosync.kinemo.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_network_members")
@Getter
@Setter
@NoArgsConstructor
public class CareNetworkMemberPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "caregiver_id", nullable = false)
    private Long caregiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareNetworkRole role;

    @Convert(converter = PermissionSetConverter.class)
    @Column(columnDefinition = "TEXT")
    private PermissionSet permissions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "care_network_id", nullable = false)
    private CareNetworkPersistenceEntity careNetwork;
}
