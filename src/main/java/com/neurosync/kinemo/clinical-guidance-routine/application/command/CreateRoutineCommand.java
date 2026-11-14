package com.neurosync.kinemo.clinicalguidanceroutine.application.command;
import java.time.LocalDate;
import java.util.List;
/** A routine must be created with an ordered sequence of activities. */
public record CreateRoutineCommand(String childId, String title, LocalDate scheduledDate,
                                   List<NewActivity> activities) {
    public record NewActivity(String title, int position, int durationMinutes) {}
    public CreateRoutineCommand {
        activities = activities == null ? List.of() : List.copyOf(activities);
    }
}
