package com.neurosync.kinemo.child.interfaces.rest;

import com.neurosync.kinemo.child.application.ChildProfiles;
import com.neurosync.kinemo.child.domain.model.entities.ClinicalProfile;
import com.neurosync.kinemo.child.interfaces.rest.resources.ChildResource;
import com.neurosync.kinemo.child.interfaces.rest.resources.CreateChildResource;
import com.neurosync.kinemo.child.interfaces.rest.resources.UpdateChildResource;
import com.neurosync.kinemo.child.interfaces.rest.resources.UpdateClinicalProfileResource;
import com.neurosync.kinemo.child.interfaces.rest.transform.ChildResourceAssembler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/children")
public class ChildrenController {
    private final ChildProfiles profiles;

    public ChildrenController(ChildProfiles profiles) {
        this.profiles = profiles;
    }

    @GetMapping
    public List<ChildResource> list(Principal principal) {
        return profiles.list(actor(principal)).stream()
                .map(ChildResourceAssembler::toResource)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildResource create(
            Principal principal,
            @Valid @RequestBody CreateChildResource input
    ) {
        Long parentId = actor(principal);
        if (input.parentId() != null && !parentId.equals(input.parentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Parent ID must match the authenticated account");
        }
        validateBirthDate(input.birthDate());

        return ChildResourceAssembler.toResource(
                profiles.create(parentId, input.firstName(), input.lastName(), input.birthDate())
        );
    }

    @GetMapping("/{id}")
    public ChildResource get(Principal principal, @PathVariable Long id) {
        return ChildResourceAssembler.toResource(profiles.get(actor(principal), id));
    }

    @PutMapping("/{id}")
    public ChildResource update(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateChildResource input
    ) {
        validateBirthDate(input.birthDate());

        return ChildResourceAssembler.toResource(
                profiles.update(actor(principal), id, input.firstName(), input.lastName(), input.birthDate())
        );
    }

    @GetMapping("/{id}/clinical-profile")
    public ClinicalProfile clinical(Principal principal, @PathVariable Long id) {
        return profiles.get(actor(principal), id).getClinicalProfile();
    }

    @PutMapping("/{id}/clinical-profile")
    public ClinicalProfile updateClinical(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody UpdateClinicalProfileResource input
    ) {
        return profiles.updateProfile(
                actor(principal),
                id,
                input.specialNeeds(),
                input.triggers(),
                input.regulators()
        );
    }

    private Long actor(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        try {
            long id = Long.parseLong(principal.getName());
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
            // Account identifiers must match the identifiers used by the care network.
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verified account identifier required");
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birth date cannot be in the future");
        }
    }

}
