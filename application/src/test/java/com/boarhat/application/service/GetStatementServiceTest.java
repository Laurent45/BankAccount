package com.boarhat.application.service;

import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.statement.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetStatementServiceTest {
    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());

    private InMemoryAccountRepository accountRepository;
    private GetStatementService getStatementService;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        getStatementService = new GetStatementService(accountRepository);
    }

    @Test
    void should_return_statement_for_given_account() {
        accountRepository.save(BankAccount.create(ACCOUNT_ID));

        Statement statement = getStatementService.getStatement(ACCOUNT_ID);

        assertThat(statement.accountId()).isEqualTo(ACCOUNT_ID);
    }

    @Test
    void should_throw_when_account_not_found() {
        assertThatThrownBy(() -> getStatementService.getStatement(ACCOUNT_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }
}