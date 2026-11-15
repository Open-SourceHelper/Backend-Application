package com.neurosync.kinemo.child.application.internal.commandservices;

import com.neurosync.kinemo.child.application.commands.CreateChildCommand;
import com.neurosync.kinemo.child.application.commands.UpdateChildCommand;
import com.neurosync.kinemo.child.application.commands.UpdateClinicalProfileCommand;
import com.neurosync.kinemo.child.application.commandservices.ChildCommandService;
import com.neurosync.kinemo.child.domain.model.aggregates.Child;
import com.neurosync.kinemo.child.domain.model.entities.CareNetwork;
import com.neurosync.kinemo.child.domain.model.entities.ClinicalProfile;
import com.neurosync.kinemo.child.domain.repositories.ChildRepository;
import com.neurosync.kinemo.shared.application.result.ApplicationError;
import com.neurosync.kinemo.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChildCommandServiceImpl implements ChildCommandService {
    private final ChildRepository childRepository;

    public ChildCommandServiceImpl(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    public Result<Long, ApplicationError> handle(CreateChildCommand command) {
        var now = LocalDateTime.now();
        var child = Child.reconstitute(
                null,
                command.parentId(),
                command.firstName().strip(),
                command.lastName().strip(),
                command.birthDate(),
                now,
                new ClinicalProfile(),
                CareNetwork.reconstitute(null, "Care network", now)
        );
        return Result.success(childRepository.save(child).getId());
    }

    @Override
    public Result<Long, ApplicationError> handle(UpdateChildCommand command) {
        var child = childRepository.findForUpdate(command.childId());
        if (child.isEmpty()) {
            return Result.failure(ApplicationError.notFound("child", command.childId().toString()));
        }

        var updatedChild = child.get().updateInformation(
                command.firstName().strip(),
                command.lastName().strip(),
                command.birthDate()
        );
        return Result.success(childRepository.save(updatedChild).getId());
    }

    @Override
    public Result<Long, ApplicationError> handle(UpdateClinicalProfileCommand command) {
        var child = childRepository.findForUpdate(command.childId());
        if (child.isEmpty()) {
            return Result.failure(ApplicationError.notFound("child", command.childId().toString()));
        }

        child.get().updatedClinicalProfile(
                command.specialNeeds(),
                command.triggers(),
                command.regulators()
        );
        return Result.success(childRepository.save(child.get()).getId());
    }
}
