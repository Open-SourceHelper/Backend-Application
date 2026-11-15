package com.neurosync.kinemo.clinicalguidanceroutine.application.dto;
import java.util.*;
public record Actor(String id,Set<String> roles) {
    public Actor { Objects.requireNonNull(id); roles=Set.copyOf(roles); }
    public boolean hasRole(String name) { return roles.contains(name); }
}
