package com.boarhat.domain.account;

import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OverdraftAuthorizationTest {

    @Nested
    class Validation {

        @Test
        void should_throw_when_limit_is_negative() {
            assertThatThrownBy(() -> new OverdraftAuthorization(new BigDecimal("-1")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_represent_no_overdraft_as_zero_limit() {
            assertThat(OverdraftAuthorization.notAllowed())
                    .isEqualTo(new OverdraftAuthorization(BigDecimal.ZERO));
        }
    }

    @Nested
    class Equality {

        @Test
        void should_be_equal_when_same_limit_regardless_of_scale() {
            assertThat(new OverdraftAuthorization(new BigDecimal("200.00")))
                    .isEqualTo(OverdraftAuthorization.allowed(Amount.of(new BigDecimal("200"))));
        }
    }

    @Nested
    class AvailableBalance {

        @Test
        void should_not_change_balance_when_not_allowed() {
            Balance available = OverdraftAuthorization.notAllowed().availableBalance(Balance.of(new BigDecimal("50")));

            assertThat(available).isEqualTo(Balance.of(new BigDecimal("50")));
        }

        @Test
        void should_increase_balance_by_limit() {
            OverdraftAuthorization authorization = OverdraftAuthorization.allowed(Amount.of(new BigDecimal("100")));

            assertThat(authorization.availableBalance(Balance.of(new BigDecimal("50"))))
                    .isEqualTo(Balance.of(new BigDecimal("150")));
        }
    }
}
