package com.boarhat.domain.model;

import com.boarhat.domain.exception.DepositCeilingReachedException;
import com.boarhat.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SavingsAccountTest {

    private static final DepositCeiling CEILING = new DepositCeiling(Amount.of(new BigDecimal("1000")));

    @Test
    void should_increase_balance_when_depositing() {
        SavingsAccount account = SavingsAccount.create(new AccountId(UUID.randomUUID()), CEILING);

        account.deposit(Amount.of(new BigDecimal("500")));

        assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("500")));
    }

    @Test
    void should_allow_deposit_up_to_ceiling() {
        SavingsAccount account = SavingsAccount.create(new AccountId(UUID.randomUUID()), CEILING);

        account.deposit(Amount.of(new BigDecimal("1000")));

        assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("1000")));
    }

    @Test
    void should_throw_when_deposit_exceeds_ceiling() {
        SavingsAccount account = SavingsAccount.create(new AccountId(UUID.randomUUID()), CEILING);

        assertThatThrownBy(() -> account.deposit(Amount.of(new BigDecimal("1001"))))
                .isInstanceOf(DepositCeilingReachedException.class);
    }

    @Test
    void should_throw_when_cumulative_deposits_exceed_ceiling() {
        SavingsAccount account = SavingsAccount.create(new AccountId(UUID.randomUUID()), CEILING);
        account.deposit(Amount.of(new BigDecimal("900")));

        assertThatThrownBy(() -> account.deposit(Amount.of(new BigDecimal("200"))))
                .isInstanceOf(DepositCeilingReachedException.class);
    }

    @Test
    void should_decrease_balance_when_withdrawing() {
        SavingsAccount account = new SavingsAccount(
                new AccountId(UUID.randomUUID()),
                Balance.of(new BigDecimal("500")),
                CEILING
        );

        account.withdraw(Amount.of(new BigDecimal("200")));

        assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("300")));
    }

    @Test
    void should_allow_withdrawal_equal_to_balance() {
        SavingsAccount account = new SavingsAccount(
                new AccountId(UUID.randomUUID()),
                Balance.of(new BigDecimal("500")),
                CEILING
        );

        account.withdraw(Amount.of(new BigDecimal("500")));

        assertThat(account.getBalance()).isEqualTo(Balance.zero());
    }

    @Test
    void should_throw_InsufficientFundsException_when_withdrawal_exceeds_balance() {
        SavingsAccount account = new SavingsAccount(
                new AccountId(UUID.randomUUID()),
                Balance.of(new BigDecimal("500")),
                CEILING
        );

        assertThatThrownBy(() -> account.withdraw(Amount.of(new BigDecimal("600"))))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void should_allow_deposit_after_withdrawal_frees_room_below_ceiling() {
        SavingsAccount account = new SavingsAccount(
                new AccountId(UUID.randomUUID()),
                Balance.of(new BigDecimal("900")),
                CEILING
        );

        account.withdraw(Amount.of(new BigDecimal("500")));
        account.deposit(Amount.of(new BigDecimal("500")));

        assertThat(account.getBalance()).isEqualTo(Balance.of(new BigDecimal("900")));
    }
}
