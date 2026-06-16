package com.boarhat.infrastructure.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepositRequest(
        @Schema(description = "Amount to deposit", example = "150.00")
        @NotNull @Positive BigDecimal amount
) {
}
