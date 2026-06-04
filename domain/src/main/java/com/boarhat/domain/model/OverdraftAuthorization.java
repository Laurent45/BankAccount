package com.boarhat.domain.model;

import java.math.BigDecimal;

public record OverdraftAuthorization(Amount limit) {

    public static OverdraftAuthorization notAllowed() {
        return new OverdraftAuthorization(new Amount(BigDecimal.ZERO));
    }

    public static OverdraftAuthorization allowed(Amount amount) {
        return new OverdraftAuthorization(amount);
    }
}
