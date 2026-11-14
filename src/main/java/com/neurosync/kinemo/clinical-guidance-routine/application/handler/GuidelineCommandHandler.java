package com.neurosync.kinemo.clinicalguidanceroutine.application.handler;
import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.application.command.*;
import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.ChildAccessPort;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.ClinicalGuideline;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.ClinicalGuidelineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;
@Service
public class GuidelineCommandHandler {
    private final ClinicalGuidelineRepository guidelines;private final ChildAccessPort access;
    public GuidelineCommandHandler(ClinicalGuidelineRepository g,ChildAccessPort a){guidelines=g;access=a;}
    @Transactional public ClinicalGuideline handle(CreateGuidelineCommand cmd,Actor actor){
        requireProfessional(cmd.childId(),actor);
        return guidelines.save(ClinicalGuideline.create(cmd.childId(),actor.id(),cmd.title(),cmd.instructions(),Instant.now()));
    }
    @Transactional public ClinicalGuideline handle(UpdateGuidelineCommand cmd,Actor actor){
        ClinicalGuideline guideline=owned(cmd.guidelineId(),actor);
        return guidelines.save(guideline.edit(cmd.title(),cmd.instructions(),Instant.now()));
    }
    @Transactional public ClinicalGuideline publish(UUID id,Actor actor){
        return guidelines.save(owned(id,actor).publish(Instant.now()));
    }
    @Transactional public ClinicalGuideline archive(UUID id,Actor actor){
        return guidelines.save(owned(id,actor).archive(Instant.now()));
    }
    private ClinicalGuideline owned(UUID id,Actor actor){
        ClinicalGuideline item=guidelines.findById(id).orElseThrow(()->new UseCaseException("GUIDELINE_NOT_FOUND","No se encontró la pauta."));
        requireProfessional(item.childId(),actor);
        if(!item.authorId().equals(actor.id()))throw new UseCaseException("ACCESS_DENIED","Solo el autor puede modificar esta pauta.");
        return item;
    }
    private void requireProfessional(String childId,Actor actor){
        if(!access.isAssignedProfessional(childId,actor))throw new UseCaseException("ACCESS_DENIED","Se requiere un profesional asignado al niño.");
    }
}
