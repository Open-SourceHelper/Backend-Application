package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.ClinicalGuideline;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.ClinicalGuidelineRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaClinicalGuidelineRepositoryAdapter implements ClinicalGuidelineRepository {
    private final SpringDataClinicalGuidelineRepository jpa;
    public JpaClinicalGuidelineRepositoryAdapter(SpringDataClinicalGuidelineRepository jpa){this.jpa=jpa;}
    public Optional<ClinicalGuideline> findById(UUID id){return jpa.findById(id).map(ClinicalGuidelineJpaEntity::toDomain);}
    public List<ClinicalGuideline> findByChildId(String childId){return jpa.findByChildId(childId).stream().map(ClinicalGuidelineJpaEntity::toDomain).toList();}
    public ClinicalGuideline save(ClinicalGuideline item){
    ClinicalGuidelineJpaEntity entity=jpa.findById(item.id()).orElseGet(()->ClinicalGuidelineJpaEntity.fromDomain(item));
    entity.apply(item);return jpa.saveAndFlush(entity).toDomain();
}
}
