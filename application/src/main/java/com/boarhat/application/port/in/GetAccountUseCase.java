package com.boarhat.application.port.in;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;

public interface GetAccountUseCase {
    Account getAccount(AccountId accountId);
}
