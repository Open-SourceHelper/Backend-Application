package com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities;

import com.neurosync.kinemo.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "care_networks")
@Getter
@Setter
@NoArgsConstructor
public class CareNetworkPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "careNetwork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareNetworkInvitationPersistenceEntity> invitations = new ArrayList<>();

    @OneToMany(mappedBy = "careNetwork", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareNetworkMemberPersistenceEntity> members = new ArrayList<>();
}
