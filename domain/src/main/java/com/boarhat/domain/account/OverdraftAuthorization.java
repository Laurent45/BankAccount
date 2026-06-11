package com.boarhat.domain.account;

import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

import java.math.BigDecimal;
import java.util.Objects;

public record OverdraftAuthorization(BigDecimal limit) {

    public OverdraftAuthorization {
        Objects.requireNonNull(limit, "OverdraftAuthorization limit must not be null");
        if (limit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Overdraft limit cannot be negative");
        }
    }

    public static OverdraftAuthorization notAllowed() {
        return new OverdraftAuthorization(BigDecimal.ZERO);
    }

    public static OverdraftAuthorization allowed(Amount limit) {
        return new OverdraftAuthorization(limit.value());
    }

    public Balance availableBalance(Balance balance) {
        return Balance.of(balance.value().add(limit));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OverdraftAuthorization other)) return false;
        return this.limit.compareTo(other.limit) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(limit.stripTrailingZeros());
    }
}
