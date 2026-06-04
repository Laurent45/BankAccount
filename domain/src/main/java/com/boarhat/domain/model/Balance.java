package com.boarhat.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Balance(BigDecimal value) {

    public static Balance zero() {
        return new Balance(BigDecimal.ZERO);
    }

    public static Balance of(BigDecimal value) {
        return new Balance(value);
    }

    public Balance add(Amount amount) {
        return new Balance(this.value.add(amount.value()));
    }

    public Balance subtract(Amount amount) {
        return new Balance(this.value.subtract(amount.value()));
    }

    public boolean isNegative() {
        return this.value.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isGreaterThan(Balance other) {
        return this.value.compareTo(other.value) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Balance other)) return false;
        return this.value.compareTo(other.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value.stripTrailingZeros());
    }
}
