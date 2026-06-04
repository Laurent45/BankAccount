package com.boarhat.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

class AmountTest {

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

    @Test
    void should_be_equal_when_same_value_regardless_of_scale() {
        Amount a = Amount.of(new BigDecimal("10.00"));
        Amount b = Amount.of(new BigDecimal("10.0"));

        assertThat(a).isEqualTo(b);
    }
}
