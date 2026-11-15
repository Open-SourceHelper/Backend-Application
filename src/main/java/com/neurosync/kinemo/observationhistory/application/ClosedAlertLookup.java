package com.neurosync.kinemo.observationhistory.application;

/** Resolves whether BC03 has closed this alert for the specified child. */
public interface ClosedAlertLookup {
    boolean isClosedForChild(String alertId, String childId);
}
