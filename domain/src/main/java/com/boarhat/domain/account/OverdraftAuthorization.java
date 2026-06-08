package com.boarhat.domain.account;

import com.boarhat.domain.shared.Amount;

import java.math.BigDecimal;
import java.util.Objects;

public record OverdraftAuthorization(Amount limit) {

    public OverdraftAuthorization {
        Objects.requireNonNull(limit, "OverdraftAuthorization limit must not be null");
    }

    public static OverdraftAuthorization notAllowed() {
        return new OverdraftAuthorization(new Amount(BigDecimal.ZERO));
    }

    public static OverdraftAuthorization allowed(Amount amount) {
        Objects.requireNonNull(amount, "Overdraft limit must not be null");
        if (amount.value().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Overdraft limit must be strictly positive");
        }
        return new OverdraftAuthorization(amount);
    }
}
