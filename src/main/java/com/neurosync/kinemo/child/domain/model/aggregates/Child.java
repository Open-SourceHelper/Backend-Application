package com.neurosync.kinemo.child.domain.model.aggregates;

import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkInvitation;
import com.neurosync.kinemo.child.domain.model.entities.ClinicalProfile;
import com.neurosync.kinemo.child.domain.model.events.ClinicalProfileUpdated;
import com.neurosync.kinemo.child.domain.model.valueobjects.CareNetworkRole;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import com.neurosync.kinemo.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Child extends AbstractDomainAggregateRoot<Child> {
    private Long id;
    private Long parentId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDateTime createdAt;

    private ClinicalProfile clinicalProfile = new ClinicalProfile();
    private CareNetwork careNetwork = new CareNetwork();

    //Command

    public Child() {
        this.parentId = null;
        this.firstName = Strings.EMPTY;
        this.lastName = Strings.EMPTY;
        this.birthDate = null;
        this.createdAt = LocalDateTime.now();
    }

    public Child updateInformation(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        return this;
    }

    public ClinicalProfile updatedClinicalProfile(String needs, String triggers, String regulators) {
        this.clinicalProfile.updateProfile(needs, triggers, regulators);
        registerDomainEvent(new ClinicalProfileUpdated(this, this.id, LocalDateTime.now()));
        return this.clinicalProfile;
    }

    public void inviteCaregiver(String email, CareNetworkRole role, PermissionSet permissions) {
        careNetwork.addInvitation(new CareNetworkInvitation(email, role, permissions));
    }

    public void acceptInvitation(Long invitationId) {
        careNetwork.findInvitationById(invitationId).accept();
    }

    public void updateMemberPermissions(Long memberId, PermissionSet permissions) {
        careNetwork.findMemberById(memberId).updatePermissions(permissions);
    }

    public void revokeCaregiverAccess(Long memberId) {
        careNetwork.findMemberById(memberId).revokeAccess();
    }
}
