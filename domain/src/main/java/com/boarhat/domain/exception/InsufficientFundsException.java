package com.boarhat.domain.exception;

import com.boarhat.domain.model.AccountId;
import com.boarhat.domain.model.Amount;
import com.boarhat.domain.model.Balance;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Amount amount, AccountId accountId, Balance balance) {
        super("Cannot withdraw " + amount + " from account " + accountId + " with balance " + balance);
    }
}
