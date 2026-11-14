package com.neurosync.kinemo.clinicalguidanceroutine.domain.model;
import java.time.*;
import java.util.*;
/** Aggregate root; no Spring/JPA dependencies, as in the BC03 CrisisAlert aggregate. */
public record Routine(UUID id, String childId, String title, LocalDate scheduledDate,
                      RoutineStatus status, UUID originalRoutineId, List<RoutineActivity> activities,
                      Instant createdAt, Instant updatedAt) {
    public Routine {
        Objects.requireNonNull(id); Objects.requireNonNull(status); Objects.requireNonNull(scheduledDate);
        Objects.requireNonNull(createdAt); Objects.requireNonNull(updatedAt);
        if (childId == null || childId.isBlank() || childId.length() > 80)
            throw new DomainRuleViolation("El niño es obligatorio (máx. 80).");
        if (title == null || title.isBlank() || title.strip().length() > 120)
            throw new DomainRuleViolation("El título de la rutina es obligatorio (máx. 120).");
        childId = childId.strip(); title = title.strip();
        activities = List.copyOf(Objects.requireNonNull(activities));
        if (activities.stream().map(RoutineActivity::position).distinct().count() != activities.size())
            throw new DomainRuleViolation("No se pueden repetir posiciones de actividad.");
    }
    public static Routine create(String childId, String title, LocalDate date, Instant now) {
        return new Routine(UUID.randomUUID(),childId,title,date,RoutineStatus.DRAFT,
                null,List.of(),now,now);
    }
    private void mutable() {
        if (status == RoutineStatus.ARCHIVED) throw new DomainRuleViolation("La rutina está archivada.");
    }
    public Routine duplicate(LocalDate date, Instant now) {
        mutable();
        return new Routine(UUID.randomUUID(),childId,title,date,RoutineStatus.DRAFT,id,
                activities.stream().map(RoutineActivity::duplicate).toList(),now,now);
    }
    public Routine archive(Instant now) {
        mutable();
        return new Routine(id,childId,title,scheduledDate,RoutineStatus.ARCHIVED,
                originalRoutineId,activities,createdAt,now);
    }
    public Routine addActivity(String name, int position, int duration, Instant now) {
        mutable();
        if (activities.stream().anyMatch(a -> a.position() == position))
            throw new DomainRuleViolation("La posición ya existe.");
        List<RoutineActivity> next = new ArrayList<>(activities);
        next.add(RoutineActivity.create(name,position,duration));
        return copy(next,now);
    }
    public Routine updateActivity(UUID activityId, String name, int position, int duration, Instant now) {
        mutable();
        if (activities.stream().anyMatch(a -> !a.id().equals(activityId) && a.position() == position))
            throw new DomainRuleViolation("La posición ya existe.");
        return replace(activityId, a -> a.rename(name,position,duration),now);
    }
    public Routine removeActivity(UUID activityId, Instant now) {
        mutable(); requireActivity(activityId);
        return copy(activities.stream().filter(a -> !a.id().equals(activityId)).toList(),now);
    }
    public Routine finishActivity(UUID activityId, ActivityStatus status, Instant now) {
        mutable();
        return replace(activityId,a -> a.setStatus(status),now);
    }
    public Routine configureTimer(UUID activityId,int minutes,AlertType type,Instant now) {
        mutable(); return replace(activityId,a -> a.configureTimer(minutes,type),now);
    }
    public Routine startTimer(UUID activityId,Instant now) {
        mutable(); return replace(activityId,a -> a.startTimer(now),now);
    }
    public Routine addVisual(UUID activityId,VisualSupport visual,Instant now) {
        mutable(); return replace(activityId,a -> a.addVisual(visual),now);
    }
    public Routine removeVisual(UUID activityId,UUID visualId,Instant now) {
        mutable(); return replace(activityId,a -> a.removeVisual(visualId),now);
    }
    public RoutineActivity requireActivity(UUID activityId) {
        return activities.stream().filter(a->a.id().equals(activityId)).findFirst()
                .orElseThrow(()->new DomainRuleViolation("La actividad no pertenece a esta rutina."));
    }
    public VisualSupport requireVisual(UUID activityId,UUID visualId) {
        return requireActivity(activityId).visuals().stream().filter(v->v.id().equals(visualId))
                .findFirst().orElseThrow(()->new DomainRuleViolation("El recurso visual no existe."));
    }
    private Routine replace(UUID activityId, java.util.function.UnaryOperator<RoutineActivity> change, Instant now) {
        requireActivity(activityId);
        return copy(activities.stream().map(a->a.id().equals(activityId)?change.apply(a):a).toList(),now);
    }
    private Routine copy(List<RoutineActivity> next,Instant now) {
        RoutineStatus state = next.isEmpty() || next.stream().allMatch(a->a.status()==ActivityStatus.PENDING)
                ? RoutineStatus.DRAFT
                : next.stream().allMatch(a->a.status()!=ActivityStatus.PENDING)
                    ? RoutineStatus.COMPLETED : RoutineStatus.ACTIVE;
        return new Routine(id,childId,title,scheduledDate,state,originalRoutineId,next,createdAt,now);
    }
}
