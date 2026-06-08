package com.boarhat.application.service;

import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.port.in.GetStatementUseCase;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.port.out.AccountRepository;
import com.boarhat.domain.statement.Statement;

import java.time.LocalDateTime;

public class GetStatementService implements GetStatementUseCase {
    private final AccountRepository accountRepository;

    public GetStatementService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Statement getStatement(AccountId accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        return account.getStatement(LocalDateTime.now());
    }
}
