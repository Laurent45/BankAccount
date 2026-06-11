package com.boarhat.domain.port.in.account;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.port.out.AccountRepository;

public interface CreateBankAccountUseCase {
    AccountId createBankAccount();

    static CreateBankAccountUseCase create(AccountRepository accountRepository) {
        return new CreateBankAccountService(accountRepository);
    }
}
