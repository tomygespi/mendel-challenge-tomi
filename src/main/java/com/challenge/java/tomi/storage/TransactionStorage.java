package com.challenge.java.tomi.storage;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.validator.IEntityValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TransactionStorage {
    private static Map<Long, Transaction> TRANSACTIONS = new ConcurrentHashMap<>();

    private IEntityValidator entityValidator;

    public TransactionStorage(IEntityValidator entityValidator) {
        this.entityValidator = entityValidator;
    }

    public void save(Transaction transaction) {
        entityValidator.validate(TRANSACTIONS, transaction);
        TRANSACTIONS.put(transaction.getId(), transaction);
        System.out.println("Saved transaction: " + transaction);
    }

    public List<Transaction> findAllByType(TypeEnum typeEnum) {
        List<Transaction> foundTransactions = new ArrayList<>();
        TRANSACTIONS.forEach((id, transaction) -> {
            if (transaction.getType().equals(typeEnum)) {
                foundTransactions.add(transaction);
            }
        });
        return foundTransactions;
    }

    public Transaction findById(Long id) {
        return TRANSACTIONS.get(id);
    }

    public List<Transaction> findAllByParent(Transaction parentTransaction) {
        List<Transaction> transactions = new ArrayList<>();
        List<Transaction> filteredTransactions = new ArrayList<>();

        TRANSACTIONS.forEach((id, transaction) -> {
            if (Objects.equals(transaction.getParentId(), parentTransaction.getId())) {
                filteredTransactions.add(transaction);
            }
        });

        for (Transaction transaction: filteredTransactions) {
            if (!transactions.contains(transaction)) {
                transactions.addAll(findAllByParent(transaction));
            }
        }

        transactions.add(parentTransaction);
        return transactions;
    }

    public void deleteAll() {
        TRANSACTIONS.clear();
    }
}
