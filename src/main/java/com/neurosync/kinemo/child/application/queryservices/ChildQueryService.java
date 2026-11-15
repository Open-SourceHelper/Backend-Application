package com.neurosync.kinemo.child.application.queryservices;

import com.neurosync.kinemo.child.application.queries.GetChildByIdQuery;
import com.neurosync.kinemo.child.application.queries.GetClinicalProfileByChildIdQuery;
import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.ClinicalProfile;

import java.util.Optional;

public interface ChildQueryService {
    Optional<Child> handle(GetChildByIdQuery query);
    Optional<ClinicalProfile> handle(GetClinicalProfileByChildIdQuery query);
}
