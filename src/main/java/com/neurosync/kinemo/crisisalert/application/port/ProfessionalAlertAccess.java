package com.neurosync.kinemo.crisisalert.application.port;

import java.util.List;

/** BC03 reads active assignments through a port; professional access is checked on every request. */
public interface ProfessionalAlertAccess {
    List<String> assignedChildren(String professionalId);
    default boolean canReview(String childId, String professionalId) {
        return assignedChildren(professionalId).contains(childId);
    }
}
