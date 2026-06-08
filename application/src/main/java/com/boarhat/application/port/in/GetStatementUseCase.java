package com.boarhat.application.port.in;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.statement.Statement;

public interface GetStatementUseCase {
    Statement getStatement(AccountId accountId);
}
