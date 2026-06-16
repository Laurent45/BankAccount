package com.boarhat.infrastructure.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateOverdraftAuthorizationRequest(
        @Schema(description = "Overdraft authorization limit (how far the balance may go negative)", example = "200.00")
        @NotNull @PositiveOrZero BigDecimal limit
) {
}
