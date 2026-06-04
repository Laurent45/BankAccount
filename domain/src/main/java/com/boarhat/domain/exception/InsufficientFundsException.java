package com.boarhat.domain.exception;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Amount amount, AccountId accountId, Balance balance) {
        super("Cannot withdraw " + amount + " from account " + accountId + " with balance " + balance);
    }
}
