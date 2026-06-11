package com.boarhat.domain.account;

import com.boarhat.domain.exception.InsufficientFundsException;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.operation.OperationType;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class BankAccountTest {

    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());
    private static final OverdraftAuthorization NO_OVERDRAFT = OverdraftAuthorization.notAllowed();
    private static final Instant NOW = Instant.parse("2026-01-15T10:00:00Z");

    private BankAccount accountWithBalance(String amount) {
        return BankAccount.reconstruct(ACCOUNT_ID, Balance.of(new BigDecimal(amount)), NO_OVERDRAFT);
    }

    private BankAccount accountWithOverdraft() {
        return BankAccount.reconstruct(ACCOUNT_ID, Balance.zero(),
                OverdraftAuthorization.allowed(Amount.of(new BigDecimal("100"))));
    }

    @Nested
    class Deposit {

        @Test
        void should_increase_balance_when_depositing() {
            BankAccount account = BankAccount.create(ACCOUNT_ID);

            account.deposit(Amount.of(new BigDecimal("150.01")), NOW);

            assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("150.01")));
        }
    }

    @Nested
    class Withdrawal {

        @Test
        void should_decrease_balance_when_withdrawing() {
            BankAccount account = accountWithBalance("150.01");

            account.withdraw(Amount.of(new BigDecimal("100")), NOW);

            assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("50.01")));
        }

        @Test
        void should_allow_withdrawal_equal_to_balance() {
            BankAccount account = accountWithBalance("150.01");

            account.withdraw(Amount.of(new BigDecimal("150.01")), NOW);

            assertThat(account.getBalance()).isEqualTo(Balance.zero());
        }

        @Test
        void should_throw_when_withdrawal_exceeds_balance() {
            BankAccount account = accountWithBalance("150.01");

            assertThatThrownBy(() -> account.withdraw(Amount.of(new BigDecimal("250.01")), NOW))
                    .isInstanceOf(InsufficientFundsException.class);
        }
    }

    @Nested
    class Overdraft {

        @Test
        void should_allow_withdrawal_exceeding_balance_when_authorized() {
            BankAccount account = accountWithOverdraft();

            account.withdraw(Amount.of(new BigDecimal("100")), NOW);

            assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("-100")));
        }

        @Test
        void should_throw_when_withdrawal_exceeds_balance_and_overdraft_limit() {
            BankAccount account = accountWithOverdraft();

            assertThatThrownBy(() -> account.withdraw(Amount.of(new BigDecimal("200")), NOW))
                    .isInstanceOf(InsufficientFundsException.class);
        }

        @Test
        void should_throw_when_no_overdraft_authorized() {
            BankAccount account = accountWithBalance("0");

            assertThatThrownBy(() -> account.withdraw(Amount.of(new BigDecimal("200")), NOW))
                    .isInstanceOf(InsufficientFundsException.class);
        }

        @Test
        void should_allow_withdrawal_after_overdraft_is_authorized() {
            BankAccount account = accountWithBalance("100");

            account.updateOverdraftAuthorization(OverdraftAuthorization.allowed(Amount.of(new BigDecimal("200"))));
            account.withdraw(Amount.of(new BigDecimal("250")), NOW);

            assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("-150")));
        }

        @Test
        void should_throw_after_overdraft_authorization_is_removed() {
            BankAccount account = BankAccount.reconstruct(ACCOUNT_ID, Balance.of(new BigDecimal("100")),
                    OverdraftAuthorization.allowed(Amount.of(new BigDecimal("500"))));

            account.updateOverdraftAuthorization(OverdraftAuthorization.notAllowed());

            assertThatThrownBy(() -> account.withdraw(Amount.of(new BigDecimal("200")), NOW))
                    .isInstanceOf(InsufficientFundsException.class);
        }
    }


    @Nested
    class OperationRecording {

        @Test
        void should_return_deposit_operation() {
            BankAccount account = BankAccount.create(ACCOUNT_ID);

            Operation operation = account.deposit(Amount.of(new BigDecimal("100")), NOW);

            assertThat(operation.type()).isEqualTo(OperationType.DEPOSIT);
            assertThat(operation.amount()).isEqualTo(Amount.of(new BigDecimal("100")));
            assertThat(operation.occurredAt()).isEqualTo(NOW);
        }

        @Test
        void should_return_withdrawal_operation() {
            BankAccount account = accountWithBalance("200");

            Operation operation = account.withdraw(Amount.of(new BigDecimal("50")), NOW);

            assertThat(operation.type()).isEqualTo(OperationType.WITHDRAWAL);
            assertThat(operation.amount()).isEqualTo(Amount.of(new BigDecimal("50")));
        }

        @Test
        void should_record_balance_after_deposit() {
            BankAccount account = accountWithBalance("200");

            Operation operation = account.deposit(Amount.of(new BigDecimal("100")), NOW);

            assertThat(operation.balance()).isEqualTo(Balance.of(new BigDecimal("300")));
        }

        @Test
        void should_not_change_balance_on_failed_withdrawal() {
            BankAccount account = BankAccount.create(ACCOUNT_ID);

            assertThatThrownBy(() -> account.withdraw(Amount.of(new BigDecimal("100")), NOW))
                    .isInstanceOf(InsufficientFundsException.class);

            assertThat(account.getBalance()).isEqualTo(Balance.zero());
        }
    }
}
