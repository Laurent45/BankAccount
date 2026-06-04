package com.boarhat.domain.shared;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmountTest {

    @Nested
    class Validation {

        @Test
        void should_throw_when_amount_is_negative() {
            assertThatThrownBy(() -> Amount.of(new BigDecimal("-1")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_allow_zero_amount() {
            assertThatCode(() -> Amount.of(BigDecimal.ZERO))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class Equality {

        @Test
        void should_be_equal_when_same_value_regardless_of_scale() {
            assertThat(Amount.of(new BigDecimal("10.00")))
                    .isEqualTo(Amount.of(new BigDecimal("10.0")));
        }
    }
}
