package com.neurosync.kinemo.observationhistory.application;

import java.util.Optional;

/**
 * Resolves current access using the authenticated account and the child's live care relationships.
 */
public interface HistoryAccess {
    enum Role { FAMILY, CAREGIVER, PSYCHOLOGIST }

    Optional<Role> roleFor(String accountId, String childId);
}
