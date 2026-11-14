package com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities;

import com.neurosync.kinemo.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "children")
@Getter
@Setter
@NoArgsConstructor
public class ChildPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "clinical_profile_id", nullable = false, unique = true)
    private ClinicalProfilePersistenceEntity clinicalProfile;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "care_network_id", nullable = false, unique = true)
    private CareNetworkPersistenceEntity careNetwork;
}
