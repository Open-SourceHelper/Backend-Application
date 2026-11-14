package com.neurosync.kinemo.clinicalguidanceroutine.domain.model;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class RoutineDomainTest {
    @Test void canDuplicateAndFinishActivitiesWithoutChangingOriginal(){
        Instant now=Instant.parse("2026-11-12T12:00:00Z");
        Routine original=Routine.create("child-001","Mañana",LocalDate.of(2026,11,12),now)
                .addActivity("Desayuno",1,20,now);
        Routine copy=original.duplicate(LocalDate.of(2026,11,13),now);
        assertNotEquals(original.id(),copy.id());
        assertEquals(original.id(),copy.originalRoutineId());
        assertNotEquals(original.activities().getFirst().id(),copy.activities().getFirst().id());
        Routine completed=copy.finishActivity(copy.activities().getFirst().id(),ActivityStatus.COMPLETED,now);
        assertEquals(RoutineStatus.COMPLETED,completed.status());
        assertEquals(ActivityStatus.PENDING,original.activities().getFirst().status());
        assertEquals(RoutineStatus.ARCHIVED,original.archive(now).status());
        assertThrows(DomainRuleViolation.class,()->completed.finishActivity(completed.activities().getFirst().id(),ActivityStatus.SKIPPED,now));
    }
    @Test void doesNotAllowDuplicatePositionsOrInvalidDurations(){
        Routine r=Routine.create("child-001","Mañana",LocalDate.now(),Instant.now()).addActivity("A",1,5,Instant.now());
        assertThrows(DomainRuleViolation.class,()->r.addActivity("B",1,10,Instant.now()));
        assertThrows(DomainRuleViolation.class,()->r.addActivity("C",2,0,Instant.now()));
    }
}
