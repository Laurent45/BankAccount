package com.boarhat.application.port.in;

import com.boarhat.application.command.DepositCommand;

public interface DepositUseCase {
    void deposit(DepositCommand command);
}
