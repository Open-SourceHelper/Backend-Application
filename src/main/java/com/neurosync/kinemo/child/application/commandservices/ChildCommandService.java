package com.neurosync.kinemo.child.application.commandservices;

import com.neurosync.kinemo.child.application.commands.CreateChildCommand;
import com.neurosync.kinemo.child.application.commands.UpdateChildCommand;
import com.neurosync.kinemo.child.application.commands.UpdateClinicalProfileCommand;
import com.neurosync.kinemo.shared.application.result.ApplicationError;
import com.neurosync.kinemo.shared.application.result.Result;

public interface ChildCommandService {
    Result<Long, ApplicationError> handle(CreateChildCommand command);
    Result<Long, ApplicationError> handle(UpdateChildCommand command);
    Result<Long, ApplicationError> handle(UpdateClinicalProfileCommand command);
}
