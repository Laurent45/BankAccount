package com.boarhat.domain.fake;

import com.boarhat.domain.account.Account;
import com.boarhat.domain.account.AccountId;
import com.boarhat.domain.operation.Operation;
import com.boarhat.domain.port.out.AccountRepository;
import com.boarhat.domain.port.out.OperationRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAccountRepository implements AccountRepository, OperationRepository {

    private final Map<AccountId, Account> accounts = new HashMap<>();
    private final Map<AccountId, List<Operation>> operations = new HashMap<>();

    @Override
    public void save(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    @Override
    public void save(Account account, Operation operation) {
        accounts.put(account.getAccountId(), account);
        operations.computeIfAbsent(account.getAccountId(), id -> new ArrayList<>()).add(operation);
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    @Override
    public List<Operation> findByAccountIdSince(AccountId accountId, Instant since) {
        return operations.getOrDefault(accountId, List.of()).stream()
                .filter(operation -> operation.occurredAt().isAfter(since))
                .toList();
    }

    public Account getOrThrow(AccountId accountId) {
        return Optional.ofNullable(accounts.get(accountId)).orElseThrow();
    }

    public List<Operation> getOperations(AccountId accountId) {
        return operations.getOrDefault(accountId, List.of());
    }
}
