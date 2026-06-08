package com.boarhat.domain.account;

import com.boarhat.domain.shared.Amount;

import java.util.Objects;

public record DepositCeiling(Amount amount) {

    public DepositCeiling {
        Objects.requireNonNull(amount, "DepositCeiling amount must not be null");
    }
}
