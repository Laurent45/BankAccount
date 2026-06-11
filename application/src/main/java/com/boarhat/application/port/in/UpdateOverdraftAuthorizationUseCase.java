package com.boarhat.application.port.in;

import com.boarhat.application.command.UpdateOverdraftAuthorizationCommand;

public interface UpdateOverdraftAuthorizationUseCase {
    void updateOverdraftAuthorization(UpdateOverdraftAuthorizationCommand command);
}
