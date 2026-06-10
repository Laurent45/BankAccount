package com.boarhat.infrastructure.adapter.in.web;

import java.math.BigDecimal;

public record CreateSavingsAccountRequest(BigDecimal depositCeiling) {
}
