package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.Routine;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.RoutineRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaRoutineRepositoryAdapter implements RoutineRepository {
    private final SpringDataRoutineRepository jpa;
    public JpaRoutineRepositoryAdapter(SpringDataRoutineRepository jpa){this.jpa=jpa;}
    public Optional<Routine> findById(UUID id){return jpa.findById(id).map(RoutineJpaEntity::toDomain);}
    public List<Routine> findByChildId(String childId){return jpa.findByChildId(childId).stream().map(RoutineJpaEntity::toDomain).toList();}
    public Routine save(Routine item){
    RoutineJpaEntity entity=jpa.findById(item.id()).orElseGet(()->RoutineJpaEntity.fromDomain(item));
    entity.apply(item);return jpa.saveAndFlush(entity).toDomain();
}
}
