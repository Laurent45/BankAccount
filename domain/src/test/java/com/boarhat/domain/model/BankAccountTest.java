package com.boarhat.domain.model;

import com.boarhat.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountTest {

    @Test
    void should_increase_balance_when_depositing() {
        BankAccount bankAccount = BankAccount.create(new AccountId(UUID.randomUUID()));
        bankAccount.deposit(Money.of(new BigDecimal("150.01")));

        assertThat(bankAccount.getBalance()).isEqualTo(Money.of(new BigDecimal("150.01")));
    }

    @Test
    void should_decrease_balance_when_withdrawing() {
        BankAccount bankAccount = new BankAccount(new AccountId(UUID.randomUUID()),  Money.of(new BigDecimal("150.01")));
        bankAccount.withdraw(Money.of(new BigDecimal("100")));

        assertThat(bankAccount.getBalance()).isEqualTo(Money.of(new BigDecimal("50.01")));
    }

    @Test
    void should_allow_withdrawal_equal_to_balance() {
        BankAccount bankAccount = new BankAccount(new AccountId(UUID.randomUUID()),  Money.of(new BigDecimal("150.01")));
        bankAccount.withdraw(Money.of(new BigDecimal("150.01")));

        assertThat(bankAccount.getBalance()).isEqualTo(Money.of(BigDecimal.ZERO));
    }

    @Test
    void should_throw_InsufficientFundsException_when_withdrawal_exceeds_balance() {
        BankAccount bankAccount = new BankAccount(new AccountId(UUID.randomUUID()),  Money.of(new BigDecimal("150.01")));
        assertThatThrownBy(() -> bankAccount.withdraw(Money.of(new BigDecimal("250.01"))))
                .isInstanceOf(InsufficientFundsException.class);
    }

}