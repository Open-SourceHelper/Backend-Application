package com.neurosync.kinemo.clinicalguidanceroutine.infrastructure.storage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "evaluation_visual_bytes")
class StoredVisualBytes {
    @Id
    @Column(length = 40)
    private String id;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(nullable = false, length = 2097152)
    byte[] content;

    protected StoredVisualBytes() {}

    StoredVisualBytes(String id, byte[] content) {
        this.id = id;
        this.content = content;
    }
}
