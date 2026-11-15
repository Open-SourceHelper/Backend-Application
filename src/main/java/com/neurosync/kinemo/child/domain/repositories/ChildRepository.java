package com.neurosync.kinemo.child.domain.repositories;

import com.neurosync.kinemo.child.domain.model.aggregates.Child;

import java.util.List;
import java.util.Optional;

public interface ChildRepository {
    Optional<Child> findById(Long id);
    Optional<Child> findForUpdate(Long id);
    List<Child> findByParentId(Long parentId);
    Child save(Child child);
    boolean existsById(Long id);
}
