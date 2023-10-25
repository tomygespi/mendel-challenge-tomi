package com.challenge.java.tomi.service;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.dto.AmountSumDTO;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AmountService implements IAmountService {

    private final TransactionRepository transactionRepository;

    public AmountService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    @Transactional
    @Override
    public AmountSumDTO calculateTotalAmountByParentTransaction(Long parentTransactionId) {
        Transaction parentTransaction = this.transactionRepository.findTransactionById(parentTransactionId);
        validateParentTransaction(parentTransaction);
        List<Transaction> transactions = assignParentAndChildTransactions(parentTransaction);
        return new AmountSumDTO(transactions.stream().map(Transaction::getAmount)
                .reduce(0.0, Double::sum));
    }

    private List<Transaction> assignParentAndChildTransactions(Transaction parentTransaction) {
        List<Transaction> transactions = new ArrayList<>(List.of(parentTransaction));
        if (parentTransaction.getNestedTransactions() != null) {
            transactions.addAll(parentTransaction.getNestedTransactions());
        }
        return transactions;
    }

    private void validateParentTransaction(Transaction parentTransaction) {
        if (parentTransaction == null) {
            throw new NotFoundException("Parent transaction with given ID doesn't exist.");
        }
    }
}
