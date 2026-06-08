package com.boarhat.application.port.in;

import com.boarhat.application.command.WithdrawCommand;

public interface WithdrawUseCase {

    void withdraw(WithdrawCommand command);
}
