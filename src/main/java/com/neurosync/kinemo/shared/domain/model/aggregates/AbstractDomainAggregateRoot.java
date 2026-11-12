package com.neurosync.kinemo.shared.domain.model.aggregates;

import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collection;

@NullMarked
public class AbstractDomainAggregateRoot
        <T extends AbstractAggregateRoot<T>> extends AbstractAggregateRoot<T> {
    @Override
    protected void clearDomainEvents() {
        super.clearDomainEvents();
    }

    @Override
    protected Collection<Object> domainEvents() {
        return super.domainEvents();
    }
}
