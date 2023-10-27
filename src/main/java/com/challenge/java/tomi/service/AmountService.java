package com.challenge.java.tomi.service;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.dto.AmountSumDTO;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.storage.TransactionStorage;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AmountService implements IAmountService {

    private final TransactionStorage transactionStorage;

    public AmountService(TransactionStorage transactionStorage) {
        this.transactionStorage = transactionStorage;
    }

    @Transactional
    @Override
    public AmountSumDTO calculateTotalAmountByParentTransaction(Long parentTransactionId) {
        Transaction parentTransaction = this.transactionStorage.findById(parentTransactionId);
        validateParentTransaction(parentTransaction);
        List<Transaction> transactions = this.transactionStorage.findAllByParent(parentTransaction);
        return new AmountSumDTO(transactions.stream().map(Transaction::getAmount)
                .reduce(0.0, Double::sum));
    }

    private void validateParentTransaction(Transaction parentTransaction) {
        if (parentTransaction == null) {
            throw new NotFoundException("Parent transaction with given ID doesn't exist.");
        }
    }
}
