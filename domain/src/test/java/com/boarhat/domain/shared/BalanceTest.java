package com.boarhat.domain.shared;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceTest {

    @Nested
    class Arithmetic {

        @Test
        void should_increase_when_adding_amount() {
            Balance balance = Balance.of(new BigDecimal("100"));

            assertThat(balance.add(Amount.of(new BigDecimal("50"))))
                    .isEqualTo(Balance.of(new BigDecimal("150")));
        }

        @Test
        void should_decrease_when_subtracting_amount() {
            Balance balance = Balance.of(new BigDecimal("100"));

            assertThat(balance.subtract(Amount.of(new BigDecimal("30"))))
                    .isEqualTo(Balance.of(new BigDecimal("70")));
        }

        @Test
        void should_be_negative_when_subtraction_exceeds_value() {
            Balance balance = Balance.of(new BigDecimal("50"));

            assertThat(balance.subtract(Amount.of(new BigDecimal("100"))).isNegative()).isTrue();
        }
    }

    @Nested
    class Sign {

        @Test
        void should_return_true_when_balance_is_negative() {
            assertThat(Balance.of(new BigDecimal("-10")).isNegative()).isTrue();
        }

        @Test
        void should_return_false_when_balance_is_positive() {
            assertThat(Balance.of(new BigDecimal("10")).isNegative()).isFalse();
        }

        @Test
        void should_return_false_when_balance_is_zero() {
            assertThat(Balance.zero().isNegative()).isFalse();
        }
    }

    @Nested
    class Equality {

        @Test
        void should_be_equal_when_same_value_regardless_of_scale() {
            assertThat(Balance.of(new BigDecimal("10.00")))
                    .isEqualTo(Balance.of(new BigDecimal("10.0")));
        }
    }
}
