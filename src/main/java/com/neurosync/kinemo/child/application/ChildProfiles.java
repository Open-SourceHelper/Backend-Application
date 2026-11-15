package com.neurosync.kinemo.child.application;

import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.*;
import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import com.neurosync.kinemo.child.domain.repositories.ChildRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChildProfiles {
    private final ChildRepository children;
    private final ChildAccessService access;

    public ChildProfiles(ChildRepository children, ChildAccessService access) {
        this.children = children;
        this.access = access;
    }

    public List<Child> list(Long actor) { return children.findByParentId(actor); }

    public Child get(Long actor, Long id) {
        var child = find(id);
        if (!child.getParentId().equals(actor)
                && !access.activeCaregivers(id.toString()).contains(actor.toString())) {
            throw forbidden();
        }
        return child;
    }

    @Transactional
    public Child create(Long actor, String firstName, String lastName, LocalDate birthDate) {
        var child = Child.reconstitute(null, actor, firstName.strip(), lastName.strip(), birthDate,
                LocalDateTime.now(), new ClinicalProfile(), CareNetwork.reconstitute(null, "Care network", LocalDateTime.now()));
        return children.save(child);
    }

    @Transactional
    public Child update(Long actor, Long id, String firstName, String lastName, LocalDate birthDate) {
        return children.save(owner(actor, id, true).updateInformation(firstName.strip(), lastName.strip(), birthDate));
    }

    @Transactional
    public ClinicalProfile updateProfile(Long actor, Long id, String needs, String triggers, String regulators) {
        var child = owner(actor, id, true);
        child.updatedClinicalProfile(needs, triggers, regulators);
        return children.save(child).getClinicalProfile();
    }

    public CareNetwork network(Long actor, Long id) {
        return owner(actor, id, false).getCareNetwork();
    }

    @Transactional
    public CareNetwork invite(Long actor, Long id, String email, PermissionSet permissions) {
        var child = owner(actor, id, true);
        if (child.getCareNetwork().getInvitations().stream()
                .anyMatch(item -> item.isValid() && item.getInviteeEmail().equalsIgnoreCase(email))) {
            throw conflict("An active invitation already exists for this email");
        }
        child.inviteCaregiver(email, CareNetworkRole.CAREGIVER, permissions);
        return children.save(child).getCareNetwork();
    }

    @Transactional
    public void accept(Long actor, String verifiedEmail, Long childId, Long invitationId) {
        var child = children.findForUpdate(childId).orElseThrow(ChildProfiles::missing);
        var network = child.getCareNetwork();
        var invitation = network.getInvitations().stream()
                .filter(item -> invitationId.equals(item.getId())).findFirst().orElseThrow(ChildProfiles::missing);
        if (verifiedEmail == null || !invitation.getInviteeEmail().equalsIgnoreCase(verifiedEmail)) {
            throw forbidden();
        }
        if (!invitation.isValid() || network.getMembers().stream()
                .anyMatch(member -> member.hasAccess() && member.getCaregiverId().equals(actor))) {
            throw conflict("The invitation cannot be accepted");
        }
        child.acceptInvitation(invitationId);
        var member = new CareNetworkMember(actor, invitation.getRole());
        member.assignPermissions(invitation.getPermissions());
        network.addMember(member);
        children.save(child);
    }

    @Transactional
    public void changeMember(Long actor, Long id, Long memberId, PermissionSet permissions) {
        var child = owner(actor, id, true);
        var member = child.getCareNetwork().getMembers().stream()
                .filter(item -> memberId.equals(item.getId())).findFirst().orElseThrow(ChildProfiles::missing);
        if (permissions == null) {
            child.revokeCaregiverAccess(memberId);
        } else {
            if (!member.hasAccess()) throw conflict("Cannot modify a revoked membership");
            child.updateMemberPermissions(memberId, permissions);
        }
        children.save(child);
    }

    private Child owner(Long actor, Long id, boolean lock) {
        var child = lock ? children.findForUpdate(id).orElseThrow(ChildProfiles::missing) : find(id);
        if (!child.getParentId().equals(actor)) throw forbidden();
        return child;
    }

    private Child find(Long id) { return children.findById(id).orElseThrow(ChildProfiles::missing); }

    private static ResponseStatusException missing() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Child or care resource not found");
    }

    private static ResponseStatusException forbidden() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this child");
    }

    private static ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
