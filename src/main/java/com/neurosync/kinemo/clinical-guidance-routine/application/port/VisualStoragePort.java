package com.neurosync.kinemo.clinicalguidanceroutine.application.port;
public interface VisualStoragePort {
    record StoredVisual(String key,String filename,String mediaType) {}
    StoredVisual store(String filename,byte[] content);
    byte[] load(String key);
    void delete(String key);
}
