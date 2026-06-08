package com.boarhat.application.command;

import com.boarhat.domain.account.DepositCeiling;

public record CreateSavingsAccountCommand(DepositCeiling depositCeiling) {
}
