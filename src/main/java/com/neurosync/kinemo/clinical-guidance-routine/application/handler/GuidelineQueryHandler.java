package com.neurosync.kinemo.clinicalguidanceroutine.application.handler;
import com.neurosync.kinemo.clinicalguidanceroutine.application.UseCaseException;
import com.neurosync.kinemo.clinicalguidanceroutine.application.dto.Actor;
import com.neurosync.kinemo.clinicalguidanceroutine.application.port.ChildAccessPort;
import com.neurosync.kinemo.clinicalguidanceroutine.application.query.*;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.*;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class GuidelineQueryHandler {
    private final ClinicalGuidelineRepository guidelines;private final PracticalGuideRepository practical;
    private final ChildAccessPort access;
    public GuidelineQueryHandler(ClinicalGuidelineRepository g,PracticalGuideRepository p,ChildAccessPort a){
        guidelines=g;practical=p;access=a;
    }
    @Transactional(readOnly=true) public ClinicalGuideline get(UUID id,Actor actor){
        ClinicalGuideline item=guidelines.findById(id).orElseThrow(()->
                new UseCaseException("GUIDELINE_NOT_FOUND","No se encontró la pauta."));
        requireRead(item.childId(),actor);
        if(!visible(item,actor)) throw new UseCaseException("GUIDELINE_NOT_FOUND","No se encontró la pauta.");
        return item;
    }
    @Transactional(readOnly=true) public List<ClinicalGuideline> handle(GuidelinesForChildQuery q,Actor actor){
        requireRead(q.childId(),actor);
        return guidelines.findByChildId(q.childId()).stream().filter(g->visible(g,actor))
                .sorted(Comparator.comparing(ClinicalGuideline::updatedAt).reversed()).toList();
    }
    @Transactional(readOnly=true) public List<PracticalGuide> handle(SearchPracticalGuidesQuery q,Actor actor){
        requireRead(q.childId(),actor);
        String term=q.term()==null?"":q.term().strip().toLowerCase(Locale.ROOT);
        String category=q.category()==null?"":q.category().strip().toLowerCase(Locale.ROOT);
        return practical.findPublished().stream()
                .filter(g->category.isEmpty()||g.category().toLowerCase(Locale.ROOT).equals(category))
                .filter(g->term.isEmpty()||(g.title()+" "+g.content()).toLowerCase(Locale.ROOT).contains(term))
                .toList();
    }
    @Transactional(readOnly=true) public PracticalGuide getPractical(UUID id,String childId,Actor actor){
        requireRead(childId,actor);
        return practical.findById(id).filter(PracticalGuide::published)
                .orElseThrow(()->new UseCaseException("GUIDE_NOT_FOUND","Guía no encontrada."));
    }
    private boolean visible(ClinicalGuideline g,Actor actor){
        return g.status()==GuidelineStatus.PUBLISHED ||
                (access.isAssignedProfessional(g.childId(),actor) && g.authorId().equals(actor.id()));
    }
    private void requireRead(String childId,Actor actor){
        if(!access.mayRead(childId,actor))throw new UseCaseException("ACCESS_DENIED","Sin acceso al niño.");
    }
}
