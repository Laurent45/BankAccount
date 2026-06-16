package com.boarhat.infrastructure.adapter.in.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateSavingsAccountRequest(
        @Schema(description = "Maximum total amount that can be held on the account", example = "10000.00")
        @NotNull @Positive BigDecimal depositCeiling
) {
}
