package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access;
import jakarta.persistence.*;
@Entity @Table(name="bc04_professional_assignments")
public class ProfessionalAssignmentJpaEntity {
    @Id @Column(length=100) private String id;
    @Column(name="child_id",nullable=false,length=80) private String childId;
    @Column(name="professional_id",nullable=false,length=100) private String professionalId;
    @Column(nullable=false) private boolean active;
    protected ProfessionalAssignmentJpaEntity(){}
    public String getChildId(){ return childId; }
    public ProfessionalAssignmentJpaEntity(String id,String childId,String professionalId,boolean active){
        this.id=id;this.childId=childId;this.professionalId=professionalId;this.active=active;
    }
}
