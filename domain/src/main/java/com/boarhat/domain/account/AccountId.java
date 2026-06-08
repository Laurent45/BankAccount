package com.boarhat.domain.account;

import java.util.Objects;
import java.util.UUID;

public record AccountId(UUID value) {

    public AccountId {
        Objects.requireNonNull(value, "AccountId value must not be null");
    }
}
