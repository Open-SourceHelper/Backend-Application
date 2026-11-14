package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.VisualSupport;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="bc04_visual_supports")
public class VisualSupportJpaEntity {
    @Id private UUID id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="activity_id",nullable=false)
    private RoutineActivityJpaEntity activity;
    @Column(name="storage_key",nullable=false,length=200) private String key;
    @Column(name="filename",nullable=false,length=180) private String filename;
    @Column(name="media_type",nullable=false,length=40) private String mediaType;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    protected VisualSupportJpaEntity(){}
    public UUID getId(){return id;}
    public static VisualSupportJpaEntity fromDomain(VisualSupport d,RoutineActivityJpaEntity a){
        VisualSupportJpaEntity e=new VisualSupportJpaEntity();e.apply(d,a);return e;
    }
    public void apply(VisualSupport d,RoutineActivityJpaEntity a){
        id=d.id();activity=a;key=d.key();filename=d.filename();mediaType=d.mediaType();createdAt=d.createdAt();
    }
    public VisualSupport toDomain(){return new VisualSupport(id,key,filename,mediaType,createdAt);}
}
