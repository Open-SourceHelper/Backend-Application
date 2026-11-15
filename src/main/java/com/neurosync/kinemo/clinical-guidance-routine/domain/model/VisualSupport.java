package com.neurosync.kinemo.clinicalguidanceroutine.domain.model;
import java.time.Instant;
import java.util.*;
/** Metadata only: private asset content is in the storage adapter. */
public record VisualSupport(UUID id, String key, String filename, String mediaType, Instant createdAt) {
    public VisualSupport {
        Objects.requireNonNull(id); Objects.requireNonNull(key); Objects.requireNonNull(filename);
        Objects.requireNonNull(mediaType); Objects.requireNonNull(createdAt);
        if (key.isBlank() || filename.isBlank() || !List.of("image/png","image/jpeg").contains(mediaType))
            throw new DomainRuleViolation("El apoyo visual debe ser JPG o PNG válido.");
    }
}
