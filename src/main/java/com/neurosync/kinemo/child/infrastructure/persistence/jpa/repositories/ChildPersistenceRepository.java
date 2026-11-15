package com.neurosync.kinemo.child.infrastructure.persistence.jpa.repositories;

import com.neurosync.kinemo.child.infrastructure.persistence.jpa.entities.ChildPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ChildPersistenceRepository extends JpaRepository<ChildPersistenceEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select child from ChildPersistenceEntity child where child.id = :id")
    Optional<ChildPersistenceEntity> lockById(Long id);

    List<ChildPersistenceEntity> findByParentId(Long parentId);
}
