package com.neurosync.kinemo.clinicalguidanceroutine.application.handler;
import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.*;
import com.neurosync.kinemo.clinicalguidanceroutine.application.query.RoutinesForChildQuery;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.*;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.RoutineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class RoutineQueryHandler {
    private final RoutineRepository routines; private final ChildAccessPort access; private final VisualStoragePort storage;
    public RoutineQueryHandler(RoutineRepository r,ChildAccessPort a,VisualStoragePort s){routines=r;access=a;storage=s;}
    @Transactional(readOnly=true) public Routine get(UUID id,Actor actor){
        Routine item=routines.findById(id).orElseThrow(()->new UseCaseException("ROUTINE_NOT_FOUND","No se encontró la rutina."));
        requireRead(item.childId(),actor);return item;
    }
    @Transactional(readOnly=true) public List<Routine> handle(RoutinesForChildQuery q,Actor actor){
        requireRead(q.childId(),actor);
        return routines.findByChildId(q.childId()).stream()
                .filter(r->r.status()!=RoutineStatus.ARCHIVED)
                .filter(r->q.date()==null||r.scheduledDate().equals(q.date()))
                .sorted(Comparator.comparing(Routine::scheduledDate).thenComparing(Routine::createdAt).reversed()).toList();
    }
    @Transactional(readOnly=true) public DownloadedVisual download(UUID routineId,UUID activityId,UUID visualId,Actor actor){
        Routine item=get(routineId,actor);
        VisualSupport v=item.requireVisual(activityId,visualId);
        return new DownloadedVisual(storage.load(v.key()),v.mediaType(),v.filename());
    }
    public record DownloadedVisual(byte[] data,String mediaType,String filename){}
    private void requireRead(String childId,Actor actor){
        if(!access.mayRead(childId,actor))throw new UseCaseException("ACCESS_DENIED","Sin acceso a este niño.");
    }
}
