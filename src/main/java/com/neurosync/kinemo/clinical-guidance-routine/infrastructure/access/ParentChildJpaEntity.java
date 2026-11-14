package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access;
import jakarta.persistence.*;
@Entity @Table(name="bc04_parent_child_links")
public class ParentChildJpaEntity {
    @Id @Column(length=100) private String id;
    @Column(name="child_id",nullable=false,length=80) private String childId;
    @Column(name="parent_id",nullable=false,length=100) private String parentId;
    @Column(nullable=false) private boolean active;
    protected ParentChildJpaEntity(){}
    public ParentChildJpaEntity(String id,String childId,String parentId,boolean active){
        this.id=id;this.childId=childId;this.parentId=parentId;this.active=active;
    }
}
