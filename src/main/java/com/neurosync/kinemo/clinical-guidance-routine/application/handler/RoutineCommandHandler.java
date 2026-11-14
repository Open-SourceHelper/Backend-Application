package com.neurosync.kinemo.clinicalguidanceroutine.application.handler;
import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.application.command.*;
import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.*;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.*;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.RoutineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
@Service
public class RoutineCommandHandler {
    private final RoutineRepository routines; private final ChildAccessPort access; private final VisualStoragePort storage;
    public RoutineCommandHandler(RoutineRepository routines,ChildAccessPort access,VisualStoragePort storage){
        this.routines=routines;this.access=access;this.storage=storage;
    }
    @Transactional public Routine handle(CreateRoutineCommand cmd,Actor actor) {
        requireCreate(cmd.childId(),actor);
        if (cmd.activities().isEmpty())
            throw new UseCaseException("INVALID_REQUEST", "Agrega al menos una actividad a la rutina.");
        Instant now = Instant.now();
        Routine routine = Routine.create(cmd.childId(),cmd.title(),cmd.scheduledDate(),now);
        for (CreateRoutineCommand.NewActivity activity : cmd.activities()) {
            routine = routine.addActivity(activity.title(),activity.position(),activity.durationMinutes(),now);
        }
        return routines.save(routine);
    }
    @Transactional public Routine duplicate(UUID id,LocalDate date,Actor actor) {
        Routine item=require(id);requireCreate(item.childId(),actor);
        return routines.save(item.duplicate(date,Instant.now()));
    }
    @Transactional public Routine archive(UUID id,Actor actor) {
        Routine item=require(id);requireCreate(item.childId(),actor);
        // A repeated DELETE remains idempotent while retaining access checks.
        return item.status() == RoutineStatus.ARCHIVED ? item : routines.save(item.archive(Instant.now()));
    }
    @Transactional public Routine handle(AddActivityCommand cmd,Actor actor) {
        Routine item=require(cmd.routineId());requireCreate(item.childId(),actor);
        return routines.save(item.addActivity(cmd.title(),cmd.position(),cmd.durationMinutes(),Instant.now()));
    }
    @Transactional public Routine handle(UpdateActivityCommand cmd,Actor actor) {
        Routine item=require(cmd.routineId());requireCreate(item.childId(),actor);
        return routines.save(item.updateActivity(cmd.activityId(),cmd.title(),cmd.position(),cmd.durationMinutes(),Instant.now()));
    }
    @Transactional public Routine removeActivity(UUID id,UUID activityId,Actor actor) {
        Routine item=require(id);requireCreate(item.childId(),actor);
        return routines.save(item.removeActivity(activityId,Instant.now()));
    }
    @Transactional public Routine finishActivity(UUID id,UUID activityId,ActivityStatus status,Actor actor) {
        Routine item=require(id);requireExecute(item.childId(),actor);
        return routines.save(item.finishActivity(activityId,status,Instant.now()));
    }
    @Transactional public Routine handle(ConfigureTimerCommand cmd,Actor actor) {
        Routine item=require(cmd.routineId());requireExecute(item.childId(),actor);
        return routines.save(item.configureTimer(cmd.activityId(),cmd.transitionMinutes(),cmd.alertType(),Instant.now()));
    }
    @Transactional public Routine startTimer(UUID id,UUID activityId,Actor actor) {
        Routine item=require(id);requireExecute(item.childId(),actor);
        return routines.save(item.startTimer(activityId,Instant.now()));
    }
    @Transactional public Routine uploadVisual(UUID id,UUID activityId,String filename,byte[] content,Actor actor) {
        Routine item=require(id);requireCreate(item.childId(),actor);
        item.requireActivity(activityId);
        if(item.requireActivity(activityId).visuals().size()>=5)
            throw new UseCaseException("INVALID_REQUEST","Máximo cinco imágenes por actividad.");
        VisualStoragePort.StoredVisual stored=storage.store(filename,content);
        try {
            VisualSupport v=new VisualSupport(UUID.randomUUID(),stored.key(),stored.filename(),stored.mediaType(),Instant.now());
            return routines.save(item.addVisual(activityId,v,Instant.now()));
        } catch(RuntimeException ex){ storage.delete(stored.key()); throw ex; }
    }
    @Transactional public Routine removeVisual(UUID id,UUID activityId,UUID visualId,Actor actor) {
        Routine item=require(id);requireCreate(item.childId(),actor);
        // Do not delete the file: duplicated routines may reference the same stored visual.
        return routines.save(item.removeVisual(activityId,visualId,Instant.now()));
    }
    private Routine require(UUID id){return routines.findById(id).orElseThrow(()->
        new UseCaseException("ROUTINE_NOT_FOUND","No se encontró la rutina."));}
    private void requireCreate(String childId,Actor actor){
        if(!access.mayCreateRoutine(childId,actor))throw new UseCaseException("ACCESS_DENIED","Sin permiso para editar rutinas de este niño.");
    }
    private void requireExecute(String childId,Actor actor){
        if(!access.mayExecuteRoutine(childId,actor))throw new UseCaseException("ACCESS_DENIED","Sin acceso para ejecutar esta rutina.");
    }
}
