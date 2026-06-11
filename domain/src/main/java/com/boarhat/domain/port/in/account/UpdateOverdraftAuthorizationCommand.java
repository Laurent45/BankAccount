package com.boarhat.domain.port.in.account;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.OverdraftAuthorization;

public record UpdateOverdraftAuthorizationCommand(AccountId accountId, OverdraftAuthorization overdraftAuthorization) {
}
