package com.neurosync.kinemo.clinicalguidanceroutine.interfaces.rest;
import com.neurosync.kinemo.clinicalguidanceroutine.application.handler.GuidelineQueryHandler;
import com.neurosync.kinemo.clinicalguidanceroutine.application.query.SearchPracticalGuidesQuery;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.PracticalGuide;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/practical-guides")
public class PracticalGuidesController {
    private final GuidelineQueryHandler queries;
    public PracticalGuidesController(GuidelineQueryHandler queries){this.queries=queries;}
    @GetMapping
    public List<PracticalGuide> search(@RequestParam String childId,@RequestParam(required=false) String term,
                                      @RequestParam(required=false) String category,Authentication auth){
        return queries.handle(new SearchPracticalGuidesQuery(childId,term,category),ActorFromAuthentication.from(auth));
    }
    @GetMapping("/{id}")
    public PracticalGuide get(@PathVariable UUID id,@RequestParam String childId,Authentication auth){
        return queries.getPractical(id,childId,ActorFromAuthentication.from(auth));
    }
}
