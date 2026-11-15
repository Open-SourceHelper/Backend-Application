package com.neurosync.kinemo.child.application.internal.queryservices;

import com.neurosync.kinemo.child.application.queries.GetChildByIdQuery;
import com.neurosync.kinemo.child.application.queries.GetClinicalProfileByChildIdQuery;
import com.neurosync.kinemo.child.application.queryservices.ChildQueryService;
import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.ClinicalProfile;
import com.neurosync.kinemo.child.domain.repositories.ChildRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChildQueryServiceImpl implements ChildQueryService {
    private final ChildRepository childRepository;

    public ChildQueryServiceImpl(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    @Override
    public Optional<Child> handle(GetChildByIdQuery query) {
        return childRepository.findById(query.childId());
    }

    @Override
    public Optional<ClinicalProfile> handle(GetClinicalProfileByChildIdQuery query) {
        return childRepository.findById(query.childId()).map(Child::getClinicalProfile);
    }
}
