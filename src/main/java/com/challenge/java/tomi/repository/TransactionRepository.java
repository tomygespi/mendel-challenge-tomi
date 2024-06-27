package com.challenge.java.tomi.repository;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.validator.ITransactionValidator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRepository.class);
    private static final Map<Long, Transaction> TRANSACTIONS = new HashMap<>();

    private final ITransactionValidator transactionValidator;

    public TransactionRepository(ITransactionValidator transactionValidator) {
        this.transactionValidator = transactionValidator;
    }

    public void save(Transaction transaction) {
        this.transactionValidator.validateBeforeCreate(TRANSACTIONS, transaction);
        TRANSACTIONS.put(transaction.getId(), transaction);
        LOGGER.info(String.format("Saved transaction: %s", transaction));
    }

    public List<Transaction> findAllByType(TypeEnum typeEnum) {
        this.transactionValidator.validateType(typeEnum);
        List<Transaction> foundTransactions = new ArrayList<>();
        TRANSACTIONS.forEach((id, transaction) -> {
            if (transaction.getType().equals(typeEnum)) {
                foundTransactions.add(transaction);
            }
        });
        return foundTransactions;
    }

    public List<Transaction> findAllByParentId(Long parentTransactionId) {
        Transaction parentTransaction = this.findById(parentTransactionId);
        List<Transaction> transactions = new ArrayList<>();

        if (parentTransaction == null) return transactions;

        transactions.add(parentTransaction);
        findAllByParentId(parentTransactionId, transactions);
        return transactions;
    }

    public void findAllByParentId(Long parentTransactionId, List<Transaction> transactions) {
        TRANSACTIONS.forEach((id, transaction) -> {
            if (transaction.getParentId() != null && transaction.getParentId().equals(parentTransactionId)) {
                transactions.add(transaction);
                findAllByParentId(transaction.getId(), transactions);
            }
        });
    }

    public Transaction findById(Long id) {
        return TRANSACTIONS.get(id);
    }

    public void deleteAll() {
        TRANSACTIONS.clear();
    }
}
