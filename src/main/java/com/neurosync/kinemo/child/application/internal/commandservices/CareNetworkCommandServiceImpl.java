package com.neurosync.kinemo.child.application.internal.commandservices;

import com.neurosync.kinemo.child.application.commands.AcceptCareInvitationCommand;
import com.neurosync.kinemo.child.application.commands.InviteCaregiverCommand;
import com.neurosync.kinemo.child.application.commands.RevokeCaregiverAccessCommand;
import com.neurosync.kinemo.child.application.commands.UpdateMemberPermissionsCommand;
import com.neurosync.kinemo.child.application.commandservices.CareNetworkCommandService;
import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkInvitation;
import com.neurosync.kinemo.child.domain.model.entities.CareNetworkMember;
import com.neurosync.kinemo.child.domain.model.valueobjects.PermissionSet;
import com.neurosync.kinemo.child.domain.repositories.ChildRepository;
import com.neurosync.kinemo.shared.application.result.ApplicationError;
import com.neurosync.kinemo.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class CareNetworkCommandServiceImpl implements CareNetworkCommandService {
    private final ChildRepository childRepository;

    public CareNetworkCommandServiceImpl(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(InviteCaregiverCommand command) {
        var child = childRepository.findForUpdate(command.childId());
        if (child.isEmpty()) {
            return Result.failure(ApplicationError.notFound("child", command.childId().toString()));
        }

        var aggregate = child.get();
        boolean activeInvitationExists = aggregate.getCareNetwork().getInvitations().stream()
                .anyMatch(invitation -> invitation.isValid()
                        && invitation.getInviteeEmail().equalsIgnoreCase(command.inviteeEmail()));
        if (activeInvitationExists) {
            return Result.failure(ApplicationError.conflict(
                    "caregiver invitation",
                    "An active invitation already exists for this email"
            ));
        }

        aggregate.inviteCaregiver(
                command.inviteeEmail(),
                command.role(),
                new PermissionSet(Set.copyOf(command.permissions()))
        );

        var savedChild = childRepository.save(aggregate);
        var savedInvitation = savedChild.getCareNetwork().getInvitations().stream()
                .filter(invitation -> invitation.isValid()
                        && invitation.getInviteeEmail().equalsIgnoreCase(command.inviteeEmail()))
                .max(Comparator.comparing(CareNetworkInvitation::getCreatedAt));

        if (savedInvitation.isEmpty() || savedInvitation.get().getId() == null) {
            return Result.failure(ApplicationError.unexpected(
                    "caregiver invitation",
                    "The saved invitation identifier could not be retrieved"
            ));
        }

        return Result.success(savedInvitation.get().getId());
    }

    @Override
    public Result<Long, ApplicationError> handle(AcceptCareInvitationCommand command) {
        var child = childRepository.findForUpdate(command.childId());
        if (child.isEmpty()) {
            return Result.failure(ApplicationError.notFound("child", command.childId().toString()));
        }

        var aggregate = child.get();
        var network = aggregate.getCareNetwork();
        var invitation = network.getInvitations().stream()
                .filter(item -> command.invitationId().equals(item.getId()))
                .findFirst();
        if (invitation.isEmpty()) {
            return Result.failure(ApplicationError.notFound(
                    "care invitation",
                    command.invitationId().toString()
            ));
        }

        var careInvitation = invitation.get();
        if (!careInvitation.getInviteeEmail().equalsIgnoreCase(command.verifiedEmail())) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "invitation recipient",
                    "The verified email does not match the invitation email"
            ));
        }
        if (!careInvitation.isValid()) {
            return Result.failure(ApplicationError.conflict(
                    "caregiver invitation",
                    "The invitation is no longer valid"
            ));
        }
        if (network.getMembers().stream()
                .anyMatch(member -> member.hasAccess()
                        && command.caregiverId().equals(member.getCaregiverId()))) {
            return Result.failure(ApplicationError.conflict(
                    "care network membership",
                    "The caregiver already has active access"
            ));
        }

        aggregate.acceptInvitation(command.invitationId());
        var member = new CareNetworkMember(command.caregiverId(), careInvitation.getRole());
        member.assignPermissions(careInvitation.getPermissions());
        network.addMember(member);
        childRepository.save(aggregate);

        return Result.success(command.invitationId());
    }

    @Override
    public Result<Long, ApplicationError> handle(UpdateMemberPermissionsCommand command) {
        var child = childRepository.findForUpdate(command.childId());
        if (child.isEmpty()) {
            return Result.failure(ApplicationError.notFound("child", command.childId().toString()));
        }

        Child aggregate = child.get();
        var member = findMember(aggregate, command.memberId());
        if (member.isEmpty()) {
            return Result.failure(ApplicationError.notFound(
                    "care network member",
                    command.memberId().toString()
            ));
        }
        if (!member.get().hasAccess()) {
            return Result.failure(ApplicationError.conflict(
                    "care network member",
                    "Cannot modify a revoked membership"
            ));
        }

        aggregate.updateMemberPermissions(
                command.memberId(),
                new PermissionSet(Set.copyOf(command.permissions()))
        );
        childRepository.save(aggregate);

        return Result.success(command.memberId());
    }

    @Override
    public Result<Long, ApplicationError> handle(RevokeCaregiverAccessCommand command) {
        var child = childRepository.findForUpdate(command.childId());
        if (child.isEmpty()) {
            return Result.failure(ApplicationError.notFound("child", command.childId().toString()));
        }

        var aggregate = child.get();
        if (findMember(aggregate, command.memberId()).isEmpty()) {
            return Result.failure(ApplicationError.notFound(
                    "care network member",
                    command.memberId().toString()
            ));
        }

        aggregate.revokeCaregiverAccess(command.memberId());
        childRepository.save(aggregate);

        return Result.success(command.memberId());
    }

    private Optional<CareNetworkMember> findMember(Child child, Long memberId) {
        return child.getCareNetwork().getMembers().stream()
                .filter(member -> memberId.equals(member.getId()))
                .findFirst();
    }
}
