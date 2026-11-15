package com.neurosync.kinemo.clinicalguidanceroutine.application.port;

public interface CareNetworkAccess {
    boolean isParent(String childId, String actorId);
    boolean isCaregiver(String childId, String actorId);
}
