package com.neurosync.kinemo.clinicalguidanceroutine.domain.model;
import java.util.*;
/** Curated (not AI-generated) practical support material. */
public record PracticalGuide(UUID id,String title,String category,String content,boolean published) {
    public PracticalGuide {
        Objects.requireNonNull(id);
        if(title==null||title.isBlank()||category==null||category.isBlank()||content==null||content.isBlank())
            throw new DomainRuleViolation("La guía debe tener título, categoría e instrucciones.");
    }
}
