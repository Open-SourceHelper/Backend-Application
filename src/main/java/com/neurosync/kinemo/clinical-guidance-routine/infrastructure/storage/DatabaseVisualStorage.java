package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.storage;

import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.VisualStoragePort;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("evaluation")
class DatabaseVisualStorage implements VisualStoragePort {
    private final EntityManager entities;

    DatabaseVisualStorage(EntityManager entities) {
        this.entities = entities;
    }

    @Override
    @Transactional
    public StoredVisual store(String filename, byte[] bytes) {
        var visual = LocalVisualStorageAdapter.describe(filename, bytes);
        entities.persist(new StoredVisualBytes(visual.key(), bytes));
        return visual;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] load(String key) {
        var image = entities.find(StoredVisualBytes.class, key);
        if (image == null) throw new UseCaseException("ASSET_NOT_FOUND", "Imagen no disponible.");
        return image.content;
    }

    @Override
    @Transactional
    public void delete(String key) {
        var image = entities.find(StoredVisualBytes.class, key);
        if (image != null) entities.remove(image);
    }
}
