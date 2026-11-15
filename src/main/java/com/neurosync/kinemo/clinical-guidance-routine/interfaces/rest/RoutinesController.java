package com.neurosync.kinemo.clinicalguidanceroutine.interfaces.rest;
import com.neurosync.kinemo.clinicalguidanceroutine.application.command.*;
import com.neurosync.kinemo.clinicalguidanceroutine.application.handler.*;
import com.neurosync.kinemo.clinicalguidanceroutine.application.query.RoutinesForChildQuery;
import com.neurosync.kinemo.clinicalguidanceroutine.domain.model.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.URI;
import java.time.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/routines")
public class RoutinesController {
    private final RoutineCommandHandler commands;private final RoutineQueryHandler queries;
    public RoutinesController(RoutineCommandHandler c,RoutineQueryHandler q){commands=c;queries=q;}
    public record CreateRequest(@NotBlank @Size(max=80) String childId,
                                @NotBlank @Size(max=120) String title,
                                @NotNull LocalDate scheduledDate,
                                @NotEmpty List<@Valid ActivityRequest> activities){}
    public record DuplicateRequest(@NotNull LocalDate scheduledDate){}
    public record ActivityRequest(@NotBlank @Size(max=120) String title,
                                  @Min(1) int position,@Min(1) @Max(240) int durationMinutes){}
    public record TimerRequest(@Min(0) @Max(60) int transitionMinutes,@NotNull AlertType alertType){}
    public record ActivityResponse(UUID id,String title,int position,int durationMinutes,
                                  ActivityStatus status,int transitionMinutes,AlertType alertType,
                                  Instant timerStartedAt,List<VisualResponse> visuals){
        static ActivityResponse from(RoutineActivity a){return new ActivityResponse(a.id(),a.title(),a.position(),
          a.durationMinutes(),a.status(),a.transitionMinutes(),a.alertType(),a.timerStartedAt(),
          a.visuals().stream().map(VisualResponse::from).toList());}
    }
    public record VisualResponse(UUID id,String filename,String mediaType){
        static VisualResponse from(VisualSupport v){return new VisualResponse(v.id(),v.filename(),v.mediaType());}
    }
    public record RoutineResponse(UUID id,String childId,String title,LocalDate scheduledDate,
                                  RoutineStatus status,UUID originalRoutineId,List<ActivityResponse> activities,
                                  Instant createdAt,Instant updatedAt){
        static RoutineResponse from(Routine r){return new RoutineResponse(r.id(),r.childId(),r.title(),
          r.scheduledDate(),r.status(),r.originalRoutineId(),r.activities().stream().map(ActivityResponse::from).toList(),
          r.createdAt(),r.updatedAt());}
    }
    @PostMapping
    public ResponseEntity<RoutineResponse> create(@Valid @RequestBody CreateRequest req,Authentication auth){
        List<CreateRoutineCommand.NewActivity> activities = req.activities().stream()
                .map(a -> new CreateRoutineCommand.NewActivity(a.title(),a.position(),a.durationMinutes())).toList();
        Routine r=commands.handle(new CreateRoutineCommand(req.childId(),req.title(),req.scheduledDate(),activities),
                ActorFromAuthentication.from(auth));
        return ResponseEntity.created(URI.create("/api/v1/routines/"+r.id())).body(RoutineResponse.from(r));
    }
    @GetMapping("/{routineId}")
    public RoutineResponse get(@PathVariable UUID routineId,Authentication auth){
        return RoutineResponse.from(queries.get(routineId,ActorFromAuthentication.from(auth)));
    }
    @GetMapping
    public List<RoutineResponse> list(@RequestParam String childId,
          @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date,Authentication auth){
        return queries.handle(new RoutinesForChildQuery(childId,date),ActorFromAuthentication.from(auth))
                .stream().map(RoutineResponse::from).toList();
    }
    @PostMapping("/{routineId}/duplicate")
    public ResponseEntity<RoutineResponse> duplicate(@PathVariable UUID routineId,@Valid @RequestBody DuplicateRequest req,Authentication auth){
        Routine r=commands.duplicate(routineId,req.scheduledDate(),ActorFromAuthentication.from(auth));
        return ResponseEntity.created(URI.create("/api/v1/routines/"+r.id())).body(RoutineResponse.from(r));
    }
    @DeleteMapping("/{routineId}")
    public ResponseEntity<Void> archive(@PathVariable UUID routineId,Authentication auth){
        commands.archive(routineId,ActorFromAuthentication.from(auth));return ResponseEntity.noContent().build();
    }
    @PostMapping("/{routineId}/activities")
    public RoutineResponse add(@PathVariable UUID routineId,@Valid @RequestBody ActivityRequest req,Authentication auth){
        return RoutineResponse.from(commands.handle(new AddActivityCommand(routineId,req.title(),req.position(),req.durationMinutes()),
                ActorFromAuthentication.from(auth)));
    }
    @PutMapping("/{routineId}/activities/{activityId}")
    public RoutineResponse update(@PathVariable UUID routineId,@PathVariable UUID activityId,
            @Valid @RequestBody ActivityRequest req,Authentication auth){
        return RoutineResponse.from(commands.handle(new UpdateActivityCommand(routineId,activityId,req.title(),req.position(),req.durationMinutes()),
                ActorFromAuthentication.from(auth)));
    }
    @DeleteMapping("/{routineId}/activities/{activityId}")
    public RoutineResponse remove(@PathVariable UUID routineId,@PathVariable UUID activityId,Authentication auth){
        return RoutineResponse.from(commands.removeActivity(routineId,activityId,ActorFromAuthentication.from(auth)));
    }
    @PatchMapping("/{routineId}/activities/{activityId}/complete")
    public RoutineResponse complete(@PathVariable UUID routineId,@PathVariable UUID activityId,Authentication auth){
        return RoutineResponse.from(commands.finishActivity(routineId,activityId,ActivityStatus.COMPLETED,ActorFromAuthentication.from(auth)));
    }
    @PatchMapping("/{routineId}/activities/{activityId}/skip")
    public RoutineResponse skip(@PathVariable UUID routineId,@PathVariable UUID activityId,Authentication auth){
        return RoutineResponse.from(commands.finishActivity(routineId,activityId,ActivityStatus.SKIPPED,ActorFromAuthentication.from(auth)));
    }
    @PutMapping("/{routineId}/activities/{activityId}/timer")
    public RoutineResponse timer(@PathVariable UUID routineId,@PathVariable UUID activityId,@Valid @RequestBody TimerRequest req,Authentication auth){
        return RoutineResponse.from(commands.handle(new ConfigureTimerCommand(routineId,activityId,
                req.transitionMinutes(),req.alertType()),ActorFromAuthentication.from(auth)));
    }
    @PostMapping("/{routineId}/activities/{activityId}/timer/start")
    public RoutineResponse startTimer(@PathVariable UUID routineId,@PathVariable UUID activityId,Authentication auth){
        return RoutineResponse.from(commands.startTimer(routineId,activityId,ActorFromAuthentication.from(auth)));
    }
    @PostMapping(value="/{routineId}/activities/{activityId}/visual-supports",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public RoutineResponse upload(@PathVariable UUID routineId,@PathVariable UUID activityId,
                                  @RequestPart("file") MultipartFile file,Authentication auth) throws java.io.IOException {
        return RoutineResponse.from(commands.uploadVisual(routineId,activityId,file.getOriginalFilename(),file.getBytes(),
                ActorFromAuthentication.from(auth)));
    }
    @DeleteMapping("/{routineId}/activities/{activityId}/visual-supports/{visualId}")
    public RoutineResponse removeVisual(@PathVariable UUID routineId,@PathVariable UUID activityId,@PathVariable UUID visualId,Authentication auth){
        return RoutineResponse.from(commands.removeVisual(routineId,activityId,visualId,ActorFromAuthentication.from(auth)));
    }
    @GetMapping("/{routineId}/activities/{activityId}/visual-supports/{visualId}/file")
    public ResponseEntity<byte[]> download(@PathVariable UUID routineId,@PathVariable UUID activityId,
                                            @PathVariable UUID visualId,Authentication auth){
        RoutineQueryHandler.DownloadedVisual v=queries.download(routineId,activityId,visualId,ActorFromAuthentication.from(auth));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(v.mediaType()))
                .cacheControl(CacheControl.noStore()).body(v.data());
    }
}
