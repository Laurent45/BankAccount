package com.boarhat.domain.port.in.account;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.port.out.AccountRepository;

public interface GetAccountUseCase {
    Account getAccount(AccountId accountId);

    static GetAccountUseCase create(AccountRepository accountRepository) {
        return new GetAccountService(accountRepository);
    }
}
