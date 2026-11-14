package com.neurosync.kinemo.child.application.queryservices;

import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.ClinicalProfile;

import java.util.Optional;

public interface ChildQueryService {
    Optional<Child> getChildByIdQuery(Long childId);
    Optional<ClinicalProfile> getClinicalProfileByChildIdQuery(Long childId);
}
