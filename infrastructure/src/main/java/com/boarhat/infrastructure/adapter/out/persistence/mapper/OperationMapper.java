package com.boarhat.infrastructure.adapter.out.persistence.mapper;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;
import com.boarhat.infrastructure.adapter.out.persistence.entity.OperationJpaEntity;

public final class OperationMapper {

    private OperationMapper() {
    }

    public static OperationJpaEntity toEntity(AccountId accountId, Operation operation) {
        return new OperationJpaEntity(
                accountId.value(),
                operation.type(),
                operation.amount().value(),
                operation.balance().value(),
                operation.occurredAt());
    }

    public static Operation toDomain(OperationJpaEntity entity) {
        return new Operation(
                entity.getType(),
                Amount.of(entity.getAmount()),
                Balance.of(entity.getBalance()),
                entity.getOccurredAt());
    }
}
