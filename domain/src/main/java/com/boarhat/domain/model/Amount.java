package com.boarhat.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Amount(BigDecimal value) {
    public static Amount of(BigDecimal value) {
        return new Amount(value);
    }

    public Amount {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
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
