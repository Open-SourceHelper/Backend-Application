package com.neurosync.kinemo.child.interfaces.rest;

import com.neurosync.kinemo.child.application.ChildProfiles;
import com.neurosync.kinemo.child.application.VerifiedChildAccount;
import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;
import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import com.neurosync.kinemo.child.interfaces.rest.resources.CareNetworkResource;
import com.neurosync.kinemo.child.interfaces.rest.resources.InviteCaregiverResource;
import com.neurosync.kinemo.child.interfaces.rest.resources.UpdatePermissionsResource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/children/{id}/care-network")
public class CareNetworkController {
    private final ChildProfiles profiles;

    public CareNetworkController(ChildProfiles profiles) {
        this.profiles = profiles;
    }

    @GetMapping
    public CareNetworkResource get(Principal principal, @PathVariable Long id) {
        return toResource(id, profiles.network(actor(principal), id));
    }

    @PostMapping("/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public CareNetworkResource invite(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody InviteCaregiverResource input
    ) {
        if (input.role() != CareNetworkRole.CAREGIVER) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only caregiver invitations are supported"
            );
        }

        CareNetwork network = profiles.invite(
                actor(principal),
                id,
                input.inviteeEmail(),
                new PermissionSet(Set.copyOf(input.permissions()))
        );
        return toResource(id, network);
    }

    @PostMapping("/invitations/{invitationId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(
            Principal principal,
            @PathVariable Long id,
            @PathVariable Long invitationId
    ) {
        Long accountId = actor(principal);
        String email = principal instanceof VerifiedChildAccount verified
                ? verified.verifiedEmail()
                : null;

        profiles.accept(accountId, email, id, invitationId);
    }

    @PutMapping("/members/{memberId}/permissions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePermissions(
            Principal principal,
            @PathVariable Long id,
            @PathVariable Long memberId,
            @Valid @RequestBody UpdatePermissionsResource input
    ) {
        profiles.changeMember(
                actor(principal),
                id,
                memberId,
                new PermissionSet(Set.copyOf(input.permissions()))
        );
    }

    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(
            Principal principal,
            @PathVariable Long id,
            @PathVariable Long memberId
    ) {
        profiles.changeMember(actor(principal), id, memberId, null);
    }

    private CareNetworkResource toResource(Long childId, CareNetwork network) {
        return new CareNetworkResource(
                network.getId(),
                childId,
                network.getMembers(),
                network.getInvitations()
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
}
