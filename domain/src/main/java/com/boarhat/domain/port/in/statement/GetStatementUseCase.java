package com.boarhat.domain.port.in.statement;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.port.out.AccountRepository;
import com.boarhat.domain.port.out.OperationRepository;
import com.boarhat.domain.statement.Statement;

public interface GetStatementUseCase {
    Statement getStatement(AccountId accountId);

    static GetStatementUseCase create(AccountRepository accountRepository,
                                      OperationRepository operationRepository) {
        return new GetStatementService(accountRepository, operationRepository);
    }
}
