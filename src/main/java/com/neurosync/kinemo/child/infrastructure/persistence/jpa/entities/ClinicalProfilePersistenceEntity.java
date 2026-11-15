package com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities;

import com.neurosync.kinemo.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinical_profiles")
@Getter
@Setter
@NoArgsConstructor
public class ClinicalProfilePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Column(name = "special_needs")
    private String specialNeeds;

    @Column(name = "triggers")
    private String triggers;

    @Column(name = "regulators")
    private String regulators;

    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
