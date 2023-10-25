package com.challenge.java.tomi.repository;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    Transaction findTransactionById(Long transactionId);

    List<Transaction> findTransactionsByType(TypeEnum type);
}
