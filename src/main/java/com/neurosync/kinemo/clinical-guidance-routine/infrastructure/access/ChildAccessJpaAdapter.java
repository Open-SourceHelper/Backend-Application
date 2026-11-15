package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.access;

import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.CareNetworkAccess;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.ChildAccessPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class ChildAccessJpaAdapter implements ChildAccessPort {
    private final SpringDataProfessionalAssignmentRepository professionals;
    private final ObjectProvider<CareNetworkAccess> careNetwork;

    public ChildAccessJpaAdapter(SpringDataProfessionalAssignmentRepository professionals,
                                 ObjectProvider<CareNetworkAccess> careNetwork) {
        this.professionals = professionals;
        this.careNetwork = careNetwork;
    }

    private boolean parent(String childId, Actor actor) {
        var access = careNetwork.getIfAvailable();
        return access != null && actor.hasRole("PARENT") && access.isParent(childId, actor.id());
    }

    private boolean caregiver(String childId, Actor actor) {
        var access = careNetwork.getIfAvailable();
        return access != null && actor.hasRole("CAREGIVER") && access.isCaregiver(childId, actor.id());
    }

    public boolean isAssignedProfessional(String childId, Actor actor) {
        return actor.hasRole("PSYCHOLOGIST")
                && professionals.existsByChildIdAndProfessionalIdAndActiveTrue(childId, actor.id());
    }

    public boolean mayRead(String childId, Actor actor) {
        return parent(childId, actor) || caregiver(childId, actor) || isAssignedProfessional(childId, actor);
    }

    public boolean mayCreateRoutine(String childId, Actor actor) {
        return parent(childId, actor) || isAssignedProfessional(childId, actor);
    }

    public boolean mayExecuteRoutine(String childId, Actor actor) {
        return mayRead(childId, actor);
    }
}
