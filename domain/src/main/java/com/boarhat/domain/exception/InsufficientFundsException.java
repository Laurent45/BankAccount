package com.boarhat.domain.exception;

import com.boarhat.domain.model.AccountId;
import com.boarhat.domain.model.Money;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Money amount, AccountId accountId, Money balance) {
        super("Cannot withdraw " + amount + " from account " + accountId + " with balance " + balance);
    }
}
