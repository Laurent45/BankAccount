package com.boarhat.domain.exception;

import com.boarhat.domain.account.AccountId;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(AccountId accountId) {
        super("Account not found: " + accountId.value());
    }
}
