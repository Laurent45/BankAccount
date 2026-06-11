package com.boarhat.infrastructure.adapter.in.web.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateOverdraftAuthorizationRequest(
        @NotNull @PositiveOrZero BigDecimal limit
) {
}
