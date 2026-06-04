package com.boarhat.domain.model;

import com.boarhat.domain.exception.DepositCeilingReachedException;
import com.boarhat.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SavingsAccountTest {

    private static final AccountId ACCOUNT_ID = new AccountId(UUID.randomUUID());
    private static final DepositCeiling CEILING = new DepositCeiling(Amount.of(new BigDecimal("1000")));

    private SavingsAccount accountWithBalance(String amount) {
        return new SavingsAccount(ACCOUNT_ID, Balance.of(new BigDecimal(amount)), CEILING);
    }

    @Nested
    class Deposit {

        @Test
        void should_increase_balance_when_depositing() {
            SavingsAccount account = SavingsAccount.create(ACCOUNT_ID, CEILING);

            account.deposit(Amount.of(new BigDecimal("500")));

            assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("500")));
        }

        @Test
        void should_allow_deposit_up_to_ceiling() {
            SavingsAccount account = SavingsAccount.create(ACCOUNT_ID, CEILING);

            account.deposit(Amount.of(new BigDecimal("1000")));

            assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("1000")));
        }

        @Test
        void should_throw_when_deposit_exceeds_ceiling() {
            SavingsAccount account = SavingsAccount.create(ACCOUNT_ID, CEILING);

            assertThatThrownBy(() -> account.deposit(Amount.of(new BigDecimal("1001"))))
                    .isInstanceOf(DepositCeilingReachedException.class);
        }

        @Test
        void should_throw_when_cumulative_deposits_exceed_ceiling() {
            SavingsAccount account = SavingsAccount.create(ACCOUNT_ID, CEILING);
            account.deposit(Amount.of(new BigDecimal("900")));

            assertThatThrownBy(() -> account.deposit(Amount.of(new BigDecimal("200"))))
                    .isInstanceOf(DepositCeilingReachedException.class);
        }

        @Test
        void should_allow_deposit_after_withdrawal_frees_room_below_ceiling() {
            SavingsAccount account = accountWithBalance("900");

            account.withdraw(Amount.of(new BigDecimal("500")));
            account.deposit(Amount.of(new BigDecimal("500")));

            assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("900")));
        }
    }

    @Nested
    class Withdrawal {

        @Test
        void should_decrease_balance_when_withdrawing() {
            SavingsAccount account = accountWithBalance("500");

            account.withdraw(Amount.of(new BigDecimal("200")));

            assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("300")));
        }

        @Test
        void should_allow_withdrawal_equal_to_balance() {
            SavingsAccount account = accountWithBalance("500");

            account.withdraw(Amount.of(new BigDecimal("500")));

            assertThat(account.getBalance()).isEqualTo(Balance.zero());
        }

        @Test
        void should_throw_when_withdrawal_exceeds_balance() {
            SavingsAccount account = accountWithBalance("500");

            assertThatThrownBy(() -> account.withdraw(Amount.of(new BigDecimal("600"))))
                    .isInstanceOf(InsufficientFundsException.class);
        }
    }

    @Nested
    class OperationRecording {

        @Test
        void should_not_record_failed_deposit() {
            DepositCeiling tightCeiling = new DepositCeiling(Amount.of(new BigDecimal("100")));
            SavingsAccount account = SavingsAccount.create(ACCOUNT_ID, tightCeiling);

            assertThatThrownBy(() -> account.deposit(Amount.of(new BigDecimal("200"))))
                    .isInstanceOf(DepositCeilingReachedException.class);

            assertThat(account.getStatement().operations()).isEmpty();
        }
    }
}
