package com.boarhat.infrastructure.adapter.out.persistence;

import com.boarhat.domain.account.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "account")
class AccountJpaEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(precision = 19, scale = 4)
    private BigDecimal overdraftLimit;

    @Column(precision = 19, scale = 4)
    private BigDecimal depositCeiling;

    protected AccountJpaEntity() {
    }

    public AccountJpaEntity(UUID id, AccountType type, BigDecimal balance,
                            BigDecimal overdraftLimit, BigDecimal depositCeiling) {
        this.id = id;
        this.type = type;
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
        this.depositCeiling = depositCeiling;
    }

    public UUID getId() {
        return id;
    }

    public AccountType getType() {
        return type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public BigDecimal getDepositCeiling() {
        return depositCeiling;
    }
}
