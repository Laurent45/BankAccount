package com.boarhat.application.service;

import com.boarhat.application.command.DepositCommand;
import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DepositServiceTest {
    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());

    private InMemoryAccountRepository accountRepository;
    private DepositService depositService;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        depositService = new DepositService(accountRepository);
    }

    @Nested
    class WhenAccountExists {

        @BeforeEach
        void givenAccountExists() {
            accountRepository.save(BankAccount.create(ACCOUNT_ID));
        }

        @Test
        void should_increase_balance_after_deposit() {
            depositService.deposit(new DepositCommand(ACCOUNT_ID, Amount.of(new BigDecimal("200"))));

            Balance balance = accountRepository.getOrThrow(ACCOUNT_ID).getBalance();
            assertThat(balance).isEqualTo(Balance.of(new BigDecimal("200")));
        }
    }

    @Nested
    class WhenAccountDoesNotExist {

        @Test
        void should_throw_when_account_not_found() {
            DepositCommand command = new DepositCommand(ACCOUNT_ID, Amount.of(new BigDecimal("100")));

            assertThatThrownBy(() -> depositService.deposit(command))
                    .isInstanceOf(AccountNotFoundException.class);
        }
    }

}