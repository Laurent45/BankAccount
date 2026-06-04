package com.boarhat.domain.model;

import com.boarhat.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class BankAccountTest {

    @Test
    void should_increase_balance_when_depositing() {
        BankAccount bankAccount = BankAccount.create(new AccountId(UUID.randomUUID()));
        Amount amount = Amount.of(new BigDecimal("150.01"));

        Balance expectedBalance = Balance.of(new BigDecimal("150.01"));

        bankAccount.deposit(amount);
        assertThat(bankAccount.getBalance()).isEqualTo(expectedBalance);
    }

    @Test
    void should_decrease_balance_when_withdrawing() {
        BankAccount bankAccount = new BankAccount(
                new AccountId(UUID.randomUUID()),
                Balance.of(new BigDecimal("150.01")),
                OverdraftAuthorization.notAllowed()
        );
        Amount amount = Amount.of(new BigDecimal("100"));

        Balance expectedBalance = Balance.of(new BigDecimal("50.01"));

        bankAccount.withdraw(amount);
        assertThat(bankAccount.getBalance()).isEqualTo(expectedBalance);
    }

    @Test
    void should_allow_withdrawal_equal_to_balance() {
        BankAccount bankAccount = new BankAccount(
                new AccountId(UUID.randomUUID()),
                Balance.of(new BigDecimal("150.01")),
                OverdraftAuthorization.notAllowed()
        );
        Amount amount = Amount.of(new BigDecimal("150.01"));

        Balance expectedBalance = Balance.zero();

        bankAccount.withdraw(amount);
        assertThat(bankAccount.getBalance()).isEqualTo(expectedBalance);
    }

    @Test
    void should_throw_InsufficientFundsException_when_withdrawal_exceeds_balance() {
        BankAccount bankAccount = new BankAccount(
                new AccountId(UUID.randomUUID()),
                Balance.of(new BigDecimal("150.01")),
                OverdraftAuthorization.notAllowed()
        );

        Amount amount = Amount.of(new BigDecimal("250.01"));

        assertThatThrownBy(() -> bankAccount.withdraw(amount))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void should_allow_withdrawal_exceeding_balance_when_overdraft_is_authorized() {
        BankAccount bankAccount = new BankAccount(
                new AccountId(UUID.randomUUID()),
                Balance.zero(),
                OverdraftAuthorization.allowed(Amount.of(new BigDecimal("100")))
        );

        Amount amount = Amount.of(new BigDecimal("100"));

        Balance expectedBalance = Balance.of(new BigDecimal("-100"));

        bankAccount.withdraw(amount);
        assertThat(bankAccount.getBalance()).isEqualTo(expectedBalance);
    }

    @Test
    void should_throw_when_withdrawal_exceeds_balance_and_overdraft_limit() {
        BankAccount bankAccount = new BankAccount(
                new AccountId(UUID.randomUUID()),
                Balance.zero(),
                OverdraftAuthorization.allowed(Amount.of(new BigDecimal("100")))
        );

        Amount amount = Amount.of(new BigDecimal("200"));

        assertThatThrownBy(() -> bankAccount.withdraw(amount))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void should_throw_when_overdraft_is_denied() {

        BankAccount bankAccount = new BankAccount(
                new AccountId(UUID.randomUUID()),
                Balance.zero(),
                OverdraftAuthorization.notAllowed()
        );

        Amount amount = Amount.of(new BigDecimal("200"));

        assertThatThrownBy(() -> bankAccount.withdraw(amount))
                .isInstanceOf(InsufficientFundsException.class);
    }

}