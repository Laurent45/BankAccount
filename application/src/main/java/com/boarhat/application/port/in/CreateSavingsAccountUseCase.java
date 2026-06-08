package com.boarhat.application.port.in;

import com.boarhat.application.command.CreateSavingsAccountCommand;
import com.boarhat.domain.account.AccountId;

public interface CreateSavingsAccountUseCase {
    AccountId createSavingsAccount(CreateSavingsAccountCommand command);
}
