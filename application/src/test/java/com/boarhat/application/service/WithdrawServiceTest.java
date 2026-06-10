package com.boarhat.application.service;

import com.boarhat.application.command.WithdrawCommand;
import com.boarhat.application.exception.AccountNotFoundException;
import com.boarhat.application.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.account.OverdraftAuthorization;
import com.boarhat.domain.exception.InsufficientFundsException;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WithdrawServiceTest {
    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());

    private InMemoryAccountRepository accountRepository;
    private WithdrawService withdrawService;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        withdrawService = new WithdrawService(accountRepository);
    }

    @Nested
    class WhenAccountExists {

        @BeforeEach
        void givenAccountExists() {
            accountRepository.save(BankAccount.reconstruct(ACCOUNT_ID, Balance.of(new BigDecimal("200")), OverdraftAuthorization.notAllowed()));
        }

        @Test
        void should_decrease_balance_after_withdrawal() {
            withdrawService.withdraw(new WithdrawCommand(ACCOUNT_ID, Amount.of(new BigDecimal("200"))));

            Balance balance = accountRepository.getOrThrow(ACCOUNT_ID).getBalance();
            assertThat(balance).isEqualTo(Balance.of(BigDecimal.ZERO));
        }

        @Test
        void should_decrease_balance_by_withdrawn_amount() {
            withdrawService.withdraw(new WithdrawCommand(ACCOUNT_ID, Amount.of(new BigDecimal("100"))));

            assertThat(accountRepository.getOrThrow(ACCOUNT_ID).getBalance())
                    .isEqualTo(Balance.of(new BigDecimal("100")));
        }

        @Test
        void should_propagate_insufficient_funds_exception() {
            assertThatThrownBy(() -> withdrawService.withdraw(new WithdrawCommand(ACCOUNT_ID, Amount.of(new BigDecimal("201")))))
                    .isInstanceOf(InsufficientFundsException.class);
        }
    }

    @Nested
    class WhenAccountDoesNotExist {

        @Test
        void should_throw_when_account_not_found() {
            WithdrawCommand command = new WithdrawCommand(ACCOUNT_ID, Amount.of(new BigDecimal("100")));

            assertThatThrownBy(() -> withdrawService.withdraw(command))
                    .isInstanceOf(AccountNotFoundException.class);
        }
    }



}