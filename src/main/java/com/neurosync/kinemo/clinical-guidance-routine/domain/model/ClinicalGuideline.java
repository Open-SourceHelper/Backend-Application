package com.neurosync.kinemo.clinicalguidanceroutine.domain.model;
import java.time.Instant;
import java.util.*;
/** Professional-authored guidance; system never generates medical instructions. */
public record ClinicalGuideline(UUID id,String childId,String authorId,String title,String instructions,
                                GuidelineStatus status,Instant createdAt,Instant updatedAt) {
    public ClinicalGuideline {
        Objects.requireNonNull(id); Objects.requireNonNull(status);
        Objects.requireNonNull(createdAt);Objects.requireNonNull(updatedAt);
        if(childId==null||childId.isBlank()||childId.length()>80||authorId==null||authorId.isBlank())
            throw new DomainRuleViolation("Niño y profesional son obligatorios.");
        if(title==null||title.isBlank()||title.strip().length()>160)
            throw new DomainRuleViolation("Título obligatorio (máx. 160).");
        if(instructions==null||instructions.isBlank()||instructions.strip().length()>4000)
            throw new DomainRuleViolation("Instrucciones obligatorias (máx. 4000).");
        childId=childId.strip();authorId=authorId.strip();title=title.strip();instructions=instructions.strip();
    }
    public static ClinicalGuideline create(String childId,String authorId,String title,String instructions,Instant now) {
        return new ClinicalGuideline(UUID.randomUUID(),childId,authorId,title,instructions,
                GuidelineStatus.DRAFT,now,now);
    }
    public ClinicalGuideline edit(String name,String detail,Instant now) {
        if(status==GuidelineStatus.ARCHIVED)throw new DomainRuleViolation("La pauta está archivada.");
        return new ClinicalGuideline(id,childId,authorId,name,detail,status,createdAt,now);
    }
    public ClinicalGuideline publish(Instant now) {
        if(status==GuidelineStatus.ARCHIVED)throw new DomainRuleViolation("No se publica una pauta archivada.");
        return new ClinicalGuideline(id,childId,authorId,title,instructions,GuidelineStatus.PUBLISHED,createdAt,now);
    }
    public ClinicalGuideline archive(Instant now) {
        if(status==GuidelineStatus.ARCHIVED)throw new DomainRuleViolation("La pauta ya está archivada.");
        return new ClinicalGuideline(id,childId,authorId,title,instructions,GuidelineStatus.ARCHIVED,createdAt,now);
    }
}
