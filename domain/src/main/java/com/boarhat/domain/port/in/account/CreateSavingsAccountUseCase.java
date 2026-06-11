package com.boarhat.domain.port.in.account;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.port.out.AccountRepository;

public interface CreateSavingsAccountUseCase {
    AccountId createSavingsAccount(CreateSavingsAccountCommand command);

    static CreateSavingsAccountUseCase create(AccountRepository accountRepository) {
        return new CreateSavingsAccountService(accountRepository);
    }
}
