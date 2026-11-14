package com.neurosync.kinemo.clinicalguidanceroutine.application.port;
import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
/** Boundary with caregivers/guardians and professional assignments (no direct cross-context domain coupling). */
public interface ChildAccessPort {
    boolean mayRead(String childId,Actor actor);
    boolean mayCreateRoutine(String childId,Actor actor);
    boolean mayExecuteRoutine(String childId,Actor actor);
    boolean isAssignedProfessional(String childId,Actor actor);
}
