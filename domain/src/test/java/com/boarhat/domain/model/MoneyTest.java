package com.boarhat.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MoneyTest {
    @Test
    public void should_add_two_amounts() {
        Money amountA = Money.of(BigDecimal.TWO);
        Money amountB = Money.of(BigDecimal.TEN);
        Money expected = Money.of(new BigDecimal("12"));

        assertThat(amountA.add(amountB)).isEqualTo(expected);
    }

    @Test
    public void should_subtract_two_amounts() {
        Money amountA = Money.of(BigDecimal.TEN);
        Money amountB = Money.of(BigDecimal.TWO);
        Money expected = Money.of(new BigDecimal("8"));

        assertThat(amountA.subtract(amountB)).isEqualTo(expected);
    }

    @Test
    public void should_return_true_when_amount_is_greater_than_other() {
        Money amountA = Money.of(BigDecimal.TEN);
        Money amountB = Money.of(BigDecimal.TWO);

        assertThat(amountA.isGreaterThan(amountB)).isTrue();
    }

    @Test
    public void should_return_false_when_amount_is_less_than_other() {
        Money amountA = Money.of(BigDecimal.TWO);
        Money amountB = Money.of(BigDecimal.TEN);

        assertThat(amountA.isGreaterThan(amountB)).isFalse();
    }

    @Test
    public void should_be_equal_when_amounts_are_the_same() {
        Money amountA = Money.of(BigDecimal.TEN);
        Money amountB = Money.of(BigDecimal.TEN);

        assertThat(amountA).isEqualTo(amountB);
    }
}