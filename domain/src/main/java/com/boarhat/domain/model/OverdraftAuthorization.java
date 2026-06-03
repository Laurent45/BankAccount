package com.boarhat.domain.model;

import java.math.BigDecimal;

public record OverdraftAuthorization(Money amount) {
    public OverdraftAuthorization {
        if  (amount.isNegative()) {
            throw new IllegalArgumentException("Overdraft amount cannot be negative");
        }
    }

    public static OverdraftAuthorization notAllowed() {
        return new OverdraftAuthorization(Money.of(BigDecimal.ZERO));
    }

    public static OverdraftAuthorization allowed(Money amount) {
        return new OverdraftAuthorization(amount);
    }
}
