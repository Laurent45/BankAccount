package com.boarhat.domain.shared;

import java.math.BigDecimal;
import java.util.Objects;

public record Amount(BigDecimal value) {

    public Amount {
        Objects.requireNonNull(value, "Amount value must not be null");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be strictly positive");
        }
    }

    public static Amount of(BigDecimal value) {
        return new Amount(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Amount other)) return false;
        return this.value.compareTo(other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value.stripTrailingZeros());
    }
}
