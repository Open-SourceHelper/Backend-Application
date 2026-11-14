package com.neurosync.kinemo.clinicalguidanceroutine.domain.model;
public class DomainRuleViolation extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DomainRuleViolation(String message) { super(message); }
}
