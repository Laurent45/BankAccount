package com.boarhat.domain.exception;

import com.boarhat.domain.model.*;

public class DepositCeilingReachedException extends RuntimeException {
    public DepositCeilingReachedException(AccountId accountId, Balance balance, Amount amount, DepositCeiling ceiling) {
        super("Cannot deposit " + amount + " on account " + accountId
                + ": balance " + balance + " would exceed ceiling " + ceiling.amount());
    }
}
