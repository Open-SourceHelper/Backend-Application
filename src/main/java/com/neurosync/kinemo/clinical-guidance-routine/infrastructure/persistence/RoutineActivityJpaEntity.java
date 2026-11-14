package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.*;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
@Entity @Table(name="bc04_routine_activities",indexes=@Index(name="idx_bc04_activity_routine",columnList="routine_id"))
public class RoutineActivityJpaEntity {
    @Id private UUID id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="routine_id",nullable=false)
    private RoutineJpaEntity routine;
    @Column(nullable=false,length=120) private String title;
    @Column(nullable=false) private int position;
    @Column(name="duration_minutes",nullable=false) private int durationMinutes;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private ActivityStatus status;
    @Column(name="transition_minutes",nullable=false) private int transitionMinutes;
    @Enumerated(EnumType.STRING) @Column(name="alert_type",nullable=false,length=20) private AlertType alertType;
    @Column(name="timer_started_at") private Instant timerStartedAt;
    @OneToMany(mappedBy="activity",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER)
    private Set<VisualSupportJpaEntity> visuals=new LinkedHashSet<>();
    protected RoutineActivityJpaEntity(){}
    public UUID getId(){return id;}
    public static RoutineActivityJpaEntity fromDomain(RoutineActivity d,RoutineJpaEntity parent){
        RoutineActivityJpaEntity e=new RoutineActivityJpaEntity();e.apply(d,parent);return e;
    }
    public void apply(RoutineActivity d,RoutineJpaEntity parent){
        id=d.id();routine=parent;title=d.title();position=d.position();durationMinutes=d.durationMinutes();
        status=d.status();transitionMinutes=d.transitionMinutes();alertType=d.alertType();timerStartedAt=d.timerStartedAt();
        Map<UUID,VisualSupportJpaEntity> existing=visuals.stream()
            .collect(Collectors.toMap(VisualSupportJpaEntity::getId,v->v));
        Set<UUID> ids=d.visuals().stream().map(VisualSupport::id).collect(Collectors.toSet());
        visuals.removeIf(v->!ids.contains(v.getId()));
        for(VisualSupport v:d.visuals()){
            VisualSupportJpaEntity entry=existing.get(v.id());
            if(entry==null){entry=VisualSupportJpaEntity.fromDomain(v,this);visuals.add(entry);}
            else entry.apply(v,this);
        }
    }
    public RoutineActivity toDomain(){
        return new RoutineActivity(id,title,position,durationMinutes,status,transitionMinutes,alertType,
                timerStartedAt,visuals.stream().map(VisualSupportJpaEntity::toDomain).toList());
    }
}
