package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access;

import com.neurosync.kinemo.clinicalguidanceroutine.application.port.CareNetworkAccess;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Profile("local")
public class LocalCareNetworkAccess implements CareNetworkAccess {
    private static final Set<String> DEMO_CAREGIVERS = Set.of("caregiver-alert-001", "caregiver-alert-002");
    private final SpringDataParentChildRepository parents;

    public LocalCareNetworkAccess(SpringDataParentChildRepository parents) {
        this.parents = parents;
    }

    public boolean isParent(String childId, String actorId) {
        return parents.existsByChildIdAndParentIdAndActiveTrue(childId, actorId);
    }

    public boolean isCaregiver(String childId, String actorId) {
        return "child-001".equals(childId) && DEMO_CAREGIVERS.contains(actorId);
    }
}
