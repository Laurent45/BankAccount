package com.boarhat.infrastructure.adapter.out.persistence;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.port.out.OperationRepository;
import com.boarhat.infrastructure.adapter.out.persistence.mapper.OperationMapper;
import com.boarhat.infrastructure.adapter.out.persistence.repository.OperationJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
class OperationPersistenceAdapter implements OperationRepository {

    private final OperationJpaRepository operationJpaRepository;

    public OperationPersistenceAdapter(OperationJpaRepository operationJpaRepository) {
        this.operationJpaRepository = operationJpaRepository;
    }

    @Override
    public List<Operation> findByAccountIdSince(AccountId accountId, LocalDateTime since) {
        return operationJpaRepository.findByAccountIdAndOccurredAtAfterOrderByOccurredAtDesc(accountId.value(), since)
                .stream()
                .map(OperationMapper::toDomain)
                .toList();
    }
}
