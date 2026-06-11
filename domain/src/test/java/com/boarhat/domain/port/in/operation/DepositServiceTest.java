package com.boarhat.domain.port.in.operation;

import com.boarhat.domain.exception.AccountNotFoundException;
import com.boarhat.domain.fake.InMemoryAccountRepository;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.account.BankAccount;
import com.boarhat.domain.account.OverdraftAuthorization;
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

class DepositServiceTest {
    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());

    private InMemoryAccountRepository accountRepository;
    private DepositUseCase depositService;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        depositService = DepositUseCase.create(accountRepository);
    }

    @Nested
    class WhenAccountExists {

        @BeforeEach
        void givenAccountExists() {
            accountRepository.save(BankAccount.reconstruct(
                    ACCOUNT_ID, Balance.of(new BigDecimal("100")), OverdraftAuthorization.notAllowed()));
        }

        @Test
        void should_increase_balance_by_deposited_amount() {
            depositService.deposit(new DepositCommand(ACCOUNT_ID, Amount.of(new BigDecimal("50"))));

            assertThat(accountRepository.getOrThrow(ACCOUNT_ID).getBalance())
                    .isEqualTo(Balance.of(new BigDecimal("150")));
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