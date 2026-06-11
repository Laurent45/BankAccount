package com.boarhat.domain.account;

import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

import java.util.Objects;

public record DepositCeiling(Amount amount) {

    public DepositCeiling {
        Objects.requireNonNull(amount, "DepositCeiling amount must not be null");
    }

    public boolean isExceededBy(Balance balance) {
        return balance.isGreaterThan(Balance.of(amount));
    }
}
