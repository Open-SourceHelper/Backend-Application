package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.*;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
@Entity @Table(name="bc04_routines",indexes=@Index(name="idx_bc04_routines_child",columnList="child_id"))
public class RoutineJpaEntity {
    @Id private UUID id;
    @Version private Long version;
    @Column(name="child_id",nullable=false,length=80) private String childId;
    @Column(nullable=false,length=120) private String title;
    @Column(name="scheduled_date",nullable=false) private LocalDate scheduledDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private RoutineStatus status;
    @Column(name="original_routine_id") private UUID originalRoutineId;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    @OneToMany(mappedBy="routine",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER)
    @OrderBy("position ASC") private List<RoutineActivityJpaEntity> activities=new ArrayList<>();
    protected RoutineJpaEntity(){}
    public static RoutineJpaEntity fromDomain(Routine item){RoutineJpaEntity e=new RoutineJpaEntity();e.apply(item);return e;}
    public void apply(Routine d){
        id=d.id();childId=d.childId();title=d.title();scheduledDate=d.scheduledDate();status=d.status();
        originalRoutineId=d.originalRoutineId();createdAt=d.createdAt();updatedAt=d.updatedAt();
        Map<UUID,RoutineActivityJpaEntity> existing=activities.stream()
                .collect(Collectors.toMap(RoutineActivityJpaEntity::getId,a->a));
        Set<UUID> keys=d.activities().stream().map(RoutineActivity::id).collect(Collectors.toSet());
        activities.removeIf(a->!keys.contains(a.getId()));
        for(RoutineActivity a:d.activities()){
            RoutineActivityJpaEntity entry=existing.get(a.id());
            if(entry==null){entry=RoutineActivityJpaEntity.fromDomain(a,this);activities.add(entry);}
            else entry.apply(a,this);
        }
    }
    public Routine toDomain(){
        return new Routine(id,childId,title,scheduledDate,status,originalRoutineId,
                activities.stream().map(RoutineActivityJpaEntity::toDomain)
                        .sorted(Comparator.comparingInt(RoutineActivity::position)).toList(),createdAt,updatedAt);
    }
}
