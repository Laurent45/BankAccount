package com.boarhat.application.exception;

import com.boarhat.domain.account.AccountId;

public class OverdraftNotSupportedException extends RuntimeException {
    public OverdraftNotSupportedException(AccountId accountId) {
        super("Overdraft is not supported for account: " + accountId.value());
    }
}
