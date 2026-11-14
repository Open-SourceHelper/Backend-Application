package com.neurosync.kinemo.child.infrastructure.persistence.jpa.repositories;

import com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities.ChildPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildPersistenceRepository extends JpaRepository<ChildPersistenceEntity, Long> {
    List<ChildPersistenceEntity> findByParentId(Long parentId);
}
