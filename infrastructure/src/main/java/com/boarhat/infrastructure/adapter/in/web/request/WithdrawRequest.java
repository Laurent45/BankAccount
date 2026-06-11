package com.boarhat.infrastructure.adapter.in.web.request;

import java.math.BigDecimal;

public record WithdrawRequest(BigDecimal amount) {
}
