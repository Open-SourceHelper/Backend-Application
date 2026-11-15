package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="bc04_clinical_guidelines",indexes=@Index(name="idx_bc04_guidelines_child",columnList="child_id"))
public class ClinicalGuidelineJpaEntity {
    @Id private UUID id;
    @Version private Long version;
    @Column(name="child_id",nullable=false,length=80) private String childId;
    @Column(name="author_id",nullable=false,length=100) private String authorId;
    @Column(nullable=false,length=160) private String title;
    @Column(nullable=false,length=4000) private String instructions;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private GuidelineStatus status;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    protected ClinicalGuidelineJpaEntity(){}
    public static ClinicalGuidelineJpaEntity fromDomain(ClinicalGuideline d){
        ClinicalGuidelineJpaEntity e=new ClinicalGuidelineJpaEntity();e.apply(d);return e;
    }
    public void apply(ClinicalGuideline d){
        id=d.id();childId=d.childId();authorId=d.authorId();title=d.title();instructions=d.instructions();
        status=d.status();createdAt=d.createdAt();updatedAt=d.updatedAt();
    }
    public ClinicalGuideline toDomain(){return new ClinicalGuideline(id,childId,authorId,title,instructions,status,createdAt,updatedAt);}
}
