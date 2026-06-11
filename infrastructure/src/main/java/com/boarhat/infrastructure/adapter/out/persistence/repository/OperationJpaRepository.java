package com.boarhat.infrastructure.adapter.out.persistence.repository;

import com.boarhat.infrastructure.adapter.out.persistence.entity.OperationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OperationJpaRepository extends JpaRepository<OperationJpaEntity, Long> {

    List<OperationJpaEntity> findByAccountIdAndOccurredAtAfterOrderByOccurredAtDesc(UUID accountId, LocalDateTime since);
}
