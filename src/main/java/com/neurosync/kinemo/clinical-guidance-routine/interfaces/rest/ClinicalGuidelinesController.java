package com.neurosync.kinemo.clinicalguidanceroutine.interfaces.rest;
import com.neurosync.kinemo.clinicalguidanceroutine.application.command.*;
import com.neurosync.kinemo.clinicalguidanceroutine.application.handler.*;
import com.neurosync.kinemo.clinicalguidanceroutine.application.query.GuidelinesForChildQuery;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.Instant;
import java.util.*;
@RestController @RequestMapping("/api/v1/clinical-guidelines")
public class ClinicalGuidelinesController {
    private final GuidelineCommandHandler commands;private final GuidelineQueryHandler queries;
    public ClinicalGuidelinesController(GuidelineCommandHandler c,GuidelineQueryHandler q){commands=c;queries=q;}
    public record CreateRequest(@NotBlank @Size(max=80) String childId,
            @NotBlank @Size(max=160) String title,@NotBlank @Size(max=4000) String instructions){}
    public record UpdateRequest(@NotBlank @Size(max=160) String title,
            @NotBlank @Size(max=4000) String instructions){}
    public record GuidelineResponse(UUID id,String childId,String authorId,String title,String instructions,
                                    GuidelineStatus status,Instant createdAt,Instant updatedAt){
        static GuidelineResponse from(ClinicalGuideline d){return new GuidelineResponse(d.id(),d.childId(),d.authorId(),
                d.title(),d.instructions(),d.status(),d.createdAt(),d.updatedAt());}
    }
    @PostMapping
    public ResponseEntity<GuidelineResponse> create(@Valid @RequestBody CreateRequest req,Authentication auth){
        ClinicalGuideline g=commands.handle(new CreateGuidelineCommand(req.childId(),req.title(),req.instructions()),
                ActorFromAuthentication.from(auth));
        return ResponseEntity.created(URI.create("/api/v1/clinical-guidelines/"+g.id())).body(GuidelineResponse.from(g));
    }
    @PutMapping("/{id}")
    public GuidelineResponse edit(@PathVariable UUID id,@Valid @RequestBody UpdateRequest req,Authentication auth){
        return GuidelineResponse.from(commands.handle(new UpdateGuidelineCommand(id,req.title(),req.instructions()),
                ActorFromAuthentication.from(auth)));
    }
    @PatchMapping("/{id}/publish")
    public GuidelineResponse publish(@PathVariable UUID id,Authentication auth){
        return GuidelineResponse.from(commands.publish(id,ActorFromAuthentication.from(auth)));
    }
    @PatchMapping("/{id}/archive")
    public GuidelineResponse archive(@PathVariable UUID id,Authentication auth){
        return GuidelineResponse.from(commands.archive(id,ActorFromAuthentication.from(auth)));
    }
    @GetMapping("/{id}")
    public GuidelineResponse get(@PathVariable UUID id,Authentication auth){
        return GuidelineResponse.from(queries.get(id,ActorFromAuthentication.from(auth)));
    }
    @GetMapping
    public List<GuidelineResponse> list(@RequestParam String childId,Authentication auth){
        return queries.handle(new GuidelinesForChildQuery(childId),ActorFromAuthentication.from(auth))
                .stream().map(GuidelineResponse::from).toList();
    }
}
