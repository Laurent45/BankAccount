package com.boarhat.domain.port.out;

import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.operation.Operation;

import java.time.Instant;
import java.util.List;

public interface OperationRepository {

    List<Operation> findByAccountIdSince(AccountId accountId, Instant since);
}
