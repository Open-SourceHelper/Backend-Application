package com.neurosync.kinemo.clinicalguidanceroutine.domain.model;
import java.time.Instant;
import java.util.*;
public record RoutineActivity(UUID id, String title, int position, int durationMinutes,
                              ActivityStatus status, int transitionMinutes, AlertType alertType,
                              Instant timerStartedAt, List<VisualSupport> visuals) {
    public RoutineActivity {
        Objects.requireNonNull(id); Objects.requireNonNull(status); Objects.requireNonNull(alertType);
        if (title == null || title.isBlank() || title.strip().length() > 120)
            throw new DomainRuleViolation("El nombre de la actividad es obligatorio (máx. 120).");
        title = title.strip();
        if (position < 1 || durationMinutes < 1 || durationMinutes > 240 || transitionMinutes < 0 || transitionMinutes > 60)
            throw new DomainRuleViolation("Orden, duración o transición inválidos.");
        visuals = List.copyOf(Objects.requireNonNull(visuals));
    }
    public static RoutineActivity create(String title, int position, int durationMinutes) {
        return new RoutineActivity(UUID.randomUUID(), title, position, durationMinutes,
                ActivityStatus.PENDING, 0, AlertType.VISUAL, null, List.of());
    }
    public RoutineActivity duplicate() {
        // Visual metadata remains private and is referenced; source activity is unchanged.
        return new RoutineActivity(UUID.randomUUID(), title, position, durationMinutes,
                ActivityStatus.PENDING, transitionMinutes, alertType, null,
                visuals.stream().map(v -> new VisualSupport(UUID.randomUUID(),v.key(),v.filename(),v.mediaType(),v.createdAt())).toList());
    }
    public RoutineActivity rename(String newTitle, int newPosition, int newDuration) {
        return new RoutineActivity(id, newTitle, newPosition, newDuration, status,
                transitionMinutes, alertType, timerStartedAt, visuals);
    }
    public RoutineActivity setStatus(ActivityStatus next) {
        if (status != ActivityStatus.PENDING)
            throw new DomainRuleViolation("Una actividad finalizada no puede volver a cambiarse.");
        if (next == ActivityStatus.PENDING) throw new DomainRuleViolation("Estado inválido.");
        return new RoutineActivity(id, title, position, durationMinutes, next,
                transitionMinutes, alertType, timerStartedAt, visuals);
    }
    public RoutineActivity configureTimer(int minutes, AlertType alert) {
        if (status != ActivityStatus.PENDING)
            throw new DomainRuleViolation("No se configura un temporizador para una actividad finalizada.");
        return new RoutineActivity(id,title,position,durationMinutes,status,minutes,alert,null,visuals);
    }
    public RoutineActivity startTimer(Instant now) {
        if (status != ActivityStatus.PENDING || transitionMinutes <= 0)
            throw new DomainRuleViolation("Primero configura un temporizador válido para una actividad pendiente.");
        return new RoutineActivity(id,title,position,durationMinutes,status,transitionMinutes,alertType,now,visuals);
    }
    public RoutineActivity addVisual(VisualSupport visual) {
        List<VisualSupport> items = new ArrayList<>(visuals);
        if (items.size() >= 5) throw new DomainRuleViolation("Máximo cinco apoyos por actividad.");
        items.add(visual);
        return new RoutineActivity(id,title,position,durationMinutes,status,transitionMinutes,alertType,timerStartedAt,items);
    }
    public RoutineActivity removeVisual(UUID visualId) {
        List<VisualSupport> items = new ArrayList<>(visuals);
        if (!items.removeIf(v -> v.id().equals(visualId)))
            throw new DomainRuleViolation("El apoyo visual no pertenece a la actividad.");
        return new RoutineActivity(id,title,position,durationMinutes,status,transitionMinutes,alertType,timerStartedAt,items);
    }
}
