package com.boarhat.domain.port.in.account;

import com.boarhat.domain.account.DepositCeiling;

public record CreateSavingsAccountCommand(DepositCeiling depositCeiling) {
}
