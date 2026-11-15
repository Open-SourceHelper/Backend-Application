package com.neurosync.kinemo.observationhistory.interfaces.rest;

import com.neurosync.kinemo.observationhistory.application.EpisodeDetails;
import com.neurosync.kinemo.observationhistory.application.HistoryService;
import com.neurosync.kinemo.observationhistory.domain.model.CrisisSeverity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/children/{childId}/episodes")
public class HistoryController {
    private final HistoryService history;

    public HistoryController(HistoryService history) {
        this.history = history;
    }

    @GetMapping
    public List<EpisodeDetails> list(Principal principal, @PathVariable @Size(max = 128) String childId,
                                     @RequestParam(defaultValue = "0") @Min(0) int page,
                                     @RequestParam(defaultValue = "25") @Min(1) @Max(100) int size) {
        return history.list(actor(principal), childId, page, size);
    }

    @GetMapping("/{id}")
    public EpisodeDetails get(Principal principal, @PathVariable @Size(max = 128) String childId,
                              @PathVariable UUID id) {
        return history.get(actor(principal), childId, id);
    }

    @PostMapping
    public ResponseEntity<EpisodeDetails> register(Principal principal,
            @PathVariable @Size(max = 128) String childId, @Valid @RequestBody RegisterEpisode request) {
        var severity = request.crisisSeverity() == null ? null
                : CrisisSeverity.valueOf(request.crisisSeverity().toUpperCase(Locale.ROOT));
        var episode = history.register(actor(principal), childId, new HistoryService.NewEpisode(
                request.alertId(), request.description(), request.occurredAt(), severity,
                request.durationMinutes(), request.trigger(), request.helped()));
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(episode.id()).toUri();
        return ResponseEntity.created(location).body(episode);
    }

    @PostMapping("/{id}/comments")
    public EpisodeDetails comment(Principal principal, @PathVariable @Size(max = 128) String childId,
                                  @PathVariable UUID id, @Valid @RequestBody CommentRequest request) {
        return history.comment(actor(principal), childId, id, request.text());
    }

    @PutMapping("/{id}/review")
    public EpisodeDetails review(Principal principal, @PathVariable @Size(max = 128) String childId,
                                 @PathVariable UUID id) {
        return history.review(actor(principal), childId, id);
    }

    private String actor(Principal principal) {
        return principal == null ? null : principal.getName();
    }

    public record RegisterEpisode(
            @Pattern(regexp = "\\S+") @Size(max = 128) String alertId,
            @NotBlank @Size(max = 1000) String description,
            @NotNull Instant occurredAt,
            @Pattern(regexp = "(?i)mild|moderate|severe") String crisisSeverity,
            @Positive Integer durationMinutes,
            @Size(max = 255) String trigger,
            @Size(max = 255) String helped) {}

    public record CommentRequest(@NotBlank @Size(max = 1000) String text) {}
}
