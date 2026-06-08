package com.boarhat.domain.account;

import com.boarhat.domain.shared.Amount;

import java.math.BigDecimal;
import java.util.Objects;

public record DepositCeiling(Amount amount) {

    public DepositCeiling {
        Objects.requireNonNull(amount, "DepositCeiling amount must not be null");
        if (amount.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("DepositCeiling must be strictly positive");
        }
    }
}
