package com.boarhat.infrastructure.adapter.out.persistence;

import com.boarhat.domain.operation.OperationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "operation")
class OperationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    protected OperationJpaEntity() {
    }

    public OperationJpaEntity(UUID accountId, OperationType type, BigDecimal amount,
                              BigDecimal balance, LocalDateTime occurredAt) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balance = balance;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public OperationType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
