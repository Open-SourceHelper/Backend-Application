package com.neurosync.kinemo.child.infrastructure.persistence.jpa.adapters;

import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.repositories.ChildRepository;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.assemblers.ChildPersistenceAssembler;
import com.neurosync.kinemo.child.infrastructure.persistence.jpa.repositories.ChildPersistenceRepository;
import com.neurosync.kinemo.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ChildRepositoryImpl implements ChildRepository {
    private final ChildPersistenceRepository childPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ChildRepositoryImpl(ChildPersistenceRepository childPersistenceRepository,
                               DomainEventPublisher domainEventPublisher) {
        this.childPersistenceRepository = childPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Child> findById(Long id) {
        return childPersistenceRepository.findById(id).map(ChildPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Child> findByParentId(Long parentId) {
        return childPersistenceRepository.findByParentId(parentId)
                .stream()
                .map(ChildPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Child save(Child child) {
        var saved = childPersistenceRepository.save(ChildPersistenceAssembler.toPersistenceFromDomain(child));
        var savedChild = ChildPersistenceAssembler.toDomainFromPersistence(saved);
        domainEventPublisher.publishAndClearEvents(child);
        return savedChild;
    }

    @Override
    public boolean existsById(Long id) {
        return childPersistenceRepository.existsById(id);
    }
}
