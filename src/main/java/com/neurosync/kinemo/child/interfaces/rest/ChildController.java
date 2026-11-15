package com.neurosync.kinemo.child.interfaces.rest;

import com.neurosync.kinemo.child.application.ChildProfiles;
import com.neurosync.kinemo.child.application.VerifiedChildAccount;
import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;
import com.neurosync.kinemo.child.domain.model.entities.ClinicalProfile;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/children")
public class ChildController {
    private final ChildProfiles profiles;

    public ChildController(ChildProfiles profiles) { this.profiles = profiles; }

    @GetMapping
    public List<ChildResource> list(Principal principal) {
        return profiles.list(actor(principal)).stream().map(ChildResource::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChildResource create(Principal principal, @Valid @RequestBody ChildInput input) {
        return ChildResource.from(profiles.create(actor(principal), input.firstName(), input.lastName(), input.birthDate()));
    }

    @GetMapping("/{id}")
    public ChildResource get(Principal principal, @PathVariable Long id) {
        return ChildResource.from(profiles.get(actor(principal), id));
    }

    @PutMapping("/{id}")
    public ChildResource update(Principal principal, @PathVariable Long id, @Valid @RequestBody ChildInput input) {
        return ChildResource.from(profiles.update(actor(principal), id, input.firstName(), input.lastName(), input.birthDate()));
    }

    @GetMapping("/{id}/clinical-profile")
    public ClinicalProfile clinical(Principal principal, @PathVariable Long id) {
        return profiles.get(actor(principal), id).getClinicalProfile();
    }

    @PutMapping("/{id}/clinical-profile")
    public ClinicalProfile updateClinical(Principal principal, @PathVariable Long id,
                                          @Valid @RequestBody ClinicalInput input) {
        return profiles.updateProfile(actor(principal), id, input.specialNeeds(), input.triggers(), input.regulators());
    }

    @GetMapping("/{id}/care-network")
    public CareNetwork network(Principal principal, @PathVariable Long id) {
        return profiles.network(actor(principal), id);
    }

    @PostMapping("/{id}/care-network/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public CareNetwork invite(Principal principal, @PathVariable Long id, @Valid @RequestBody InvitationInput input) {
        return profiles.invite(actor(principal), id, input.email().strip(), new PermissionSet(input.permissions()));
    }

    @PostMapping("/{id}/care-network/invitations/{invitationId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(Principal principal, @PathVariable Long id, @PathVariable Long invitationId) {
        Long account = actor(principal);
        String email = principal instanceof VerifiedChildAccount verified ? verified.verifiedEmail() : null;
        profiles.accept(account, email, id, invitationId);
    }

    @PutMapping("/{id}/care-network/members/{memberId}/permissions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePermissions(Principal principal, @PathVariable Long id, @PathVariable Long memberId,
                                  @Valid @RequestBody PermissionsInput input) {
        profiles.changeMember(actor(principal), id, memberId, new PermissionSet(input.permissions()));
    }

    @DeleteMapping("/{id}/care-network/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(Principal principal, @PathVariable Long id, @PathVariable Long memberId) {
        profiles.changeMember(actor(principal), id, memberId, null);
    }

    private Long actor(Principal principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        try {
            long id = Long.parseLong(principal.getName());
            if (id > 0) return id;
        } catch (NumberFormatException ignored) {
            // Account identifiers must match the identifiers used by the care network.
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Verified account identifier required");
    }

    public record ChildInput(@NotBlank @Size(max = 100) String firstName,
                             @NotBlank @Size(max = 100) String lastName,
                             @NotNull @PastOrPresent LocalDate birthDate) {}
    public record ClinicalInput(@Size(max = 255) String specialNeeds, @Size(max = 255) String triggers,
                                @Size(max = 255) String regulators) {}
    public record InvitationInput(@NotBlank @Email @Size(max = 254) String email,
                                  @NotNull @Size(max = 20) Set<@NotBlank @Size(max = 50) String> permissions) {}
    public record PermissionsInput(@NotNull @Size(max = 20) Set<@NotBlank @Size(max = 50) String> permissions) {}
    public record ChildResource(Long id, Long parentId, String firstName, String lastName, LocalDate birthDate) {
        static ChildResource from(Child child) {
            return new ChildResource(child.getId(), child.getParentId(), child.getFirstName(), child.getLastName(), child.getBirthDate());
        }
    }
}
