package com.boarhat.infrastructure.adapter.out.persistence;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.shared.Amount;
import com.boarhat.domain.shared.Balance;

final class OperationMapper {

    private OperationMapper() {
    }

    static OperationJpaEntity toEntity(AccountId accountId, Operation operation) {
        return new OperationJpaEntity(
                accountId.value(),
                operation.type(),
                operation.amount().value(),
                operation.balance().value(),
                operation.occurredAt());
    }

    static Operation toDomain(OperationJpaEntity entity) {
        return new Operation(
                entity.getType(),
                Amount.of(entity.getAmount()),
                Balance.of(entity.getBalance()),
                entity.getOccurredAt());
    }
}
