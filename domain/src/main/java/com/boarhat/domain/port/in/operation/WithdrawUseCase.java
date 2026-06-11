package com.boarhat.domain.port.in.operation;

import com.boarhat.domain.port.out.AccountRepository;

public interface WithdrawUseCase {

    void withdraw(WithdrawCommand command);

    static WithdrawUseCase create(AccountRepository accountRepository) {
        return new WithdrawService(accountRepository);
    }
}
