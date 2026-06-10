package com.boarhat.application.service;

import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.port.in.GetStatementUseCase;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.port.out.AccountRepository;
import com.boarhat.domain.port.out.OperationRepository;
import com.boarhat.domain.statement.Statement;

import java.time.LocalDateTime;
import java.util.List;

public class GetStatementService implements GetStatementUseCase {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public GetStatementService(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @Override
    public Statement getStatement(AccountId accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        LocalDateTime now = LocalDateTime.now();
        List<Operation> operations = operationRepository.findByAccountIdSince(accountId, now.minus(Statement.HISTORY_WINDOW));

        return Statement.of(account, now, operations);
    }
}
