package com.boarhat.infrastructure.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawRequest(
        @Schema(description = "Amount to withdraw", example = "50.00")
        @NotNull @Positive BigDecimal amount
) {
}
