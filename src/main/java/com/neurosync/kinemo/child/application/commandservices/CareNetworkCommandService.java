package com.neurosync.kinemo.child.application.commandservices;

import com.neurosync.kinemo.child.application.commands.AcceptCareInvitationCommand;
import com.neurosync.kinemo.child.application.commands.InviteCaregiverCommand;
import com.neurosync.kinemo.child.application.commands.RevokeCaregiverAccessCommand;
import com.neurosync.kinemo.child.application.commands.UpdateMemberPermissionsCommand;
import com.neurosync.kinemo.shared.application.result.ApplicationError;
import com.neurosync.kinemo.shared.application.result.Result;

public interface CareNetworkCommandService {
    Result<Long, ApplicationError> handle(InviteCaregiverCommand command);
    Result<Long, ApplicationError> handle(AcceptCareInvitationCommand command);
    Result<Long, ApplicationError> handle(UpdateMemberPermissionsCommand command);
    Result<Long, ApplicationError> handle(RevokeCaregiverAccessCommand command);
}
