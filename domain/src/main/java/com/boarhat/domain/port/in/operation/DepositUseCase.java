package com.boarhat.domain.port.in.operation;

import com.boarhat.domain.port.out.AccountRepository;

public interface DepositUseCase {
    void deposit(DepositCommand command);

    static DepositUseCase create(AccountRepository accountRepository) {
        return new DepositService(accountRepository);
    }
}
