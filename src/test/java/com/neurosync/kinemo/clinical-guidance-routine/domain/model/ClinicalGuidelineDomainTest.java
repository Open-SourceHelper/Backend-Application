package com.neurosync.kinemo.clinicalguidanceroutine.domain.model;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
class ClinicalGuidelineDomainTest {
    @Test void guidelineLifecycleEnforcesArchive(){
        Instant now=Instant.now();
        ClinicalGuideline guideline=ClinicalGuideline.create("child-001","psychologist-guide-001",
                "Apoyo personalizado","Seguir indicación profesional.",now);
        assertEquals(GuidelineStatus.DRAFT,guideline.status());
        ClinicalGuideline published=guideline.publish(now);
        assertEquals(GuidelineStatus.PUBLISHED,published.status());
        ClinicalGuideline archived=published.archive(now);
        assertThrows(DomainRuleViolation.class,()->archived.edit("Otro","Otro texto",now));
        assertEquals(GuidelineStatus.DRAFT,guideline.status());
    }
}
