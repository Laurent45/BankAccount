package com.boarhat.application.service;

import com.boarhat.application.command.CreateSavingsAccountCommand;
import com.boarhat.application.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.DepositCeiling;
import com.boarhat.domain.account.SavingsAccount;
import com.boarhat.domain.shared.Amount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSavingsAccountServiceTest {
    private static final DepositCeiling DEPOSIT_CEILING = new DepositCeiling(Amount.of(new BigDecimal("300")));

    private InMemoryAccountRepository accountRepository;
    private CreateSavingsAccountService createSavingsAccountService;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        createSavingsAccountService = new CreateSavingsAccountService(accountRepository);
    }

    @Test
    void should_save_account_and_return_its_id() {
        CreateSavingsAccountCommand command = new CreateSavingsAccountCommand(DEPOSIT_CEILING);
        AccountId accountId = createSavingsAccountService.createSavingsAccount(command);

        Account saved = accountRepository.getOrThrow(accountId);
        assertThat(saved.getAccountId()).isEqualTo(accountId);
    }

    @Test
    void should_generate_unique_id_for_each_account() {
        CreateSavingsAccountCommand command = new CreateSavingsAccountCommand(DEPOSIT_CEILING);
        AccountId first = createSavingsAccountService.createSavingsAccount(command);
        AccountId second = createSavingsAccountService.createSavingsAccount(command);

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void should_save_account_with_given_deposit_ceiling() {
        CreateSavingsAccountCommand command = new CreateSavingsAccountCommand(DEPOSIT_CEILING);
        AccountId accountId = createSavingsAccountService.createSavingsAccount(command);

        SavingsAccount saved = (SavingsAccount) accountRepository.getOrThrow(accountId);
        assertThat(saved.getDepositCeiling()).isEqualTo(DEPOSIT_CEILING);
    }

}