package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.persistence;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.PracticalGuide;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.PracticalGuideRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public class JpaPracticalGuideRepositoryAdapter implements PracticalGuideRepository {
    private final SpringDataPracticalGuideRepository jpa;
    public JpaPracticalGuideRepositoryAdapter(SpringDataPracticalGuideRepository jpa){this.jpa=jpa;}
    public Optional<PracticalGuide> findById(UUID id){return jpa.findById(id).map(PracticalGuideJpaEntity::toDomain);}
    public List<PracticalGuide> findPublished(){return jpa.findByPublishedTrue().stream().map(PracticalGuideJpaEntity::toDomain).toList();}

}
