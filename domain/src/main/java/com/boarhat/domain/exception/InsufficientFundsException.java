package com.boarhat.domain.exception;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(AccountId accountId, Balance balance, Amount amount) {
        super("Cannot withdraw " + amount.value() + " from account " + accountId.value()
                + " with balance " + balance.value());
    }
}
