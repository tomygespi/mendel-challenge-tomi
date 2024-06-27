package com.challenge.java.tomi.service;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.dto.AmountSumDTO;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.repository.TransactionRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AmountService implements IAmountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmountService.class);
    private final TransactionRepository transactionRepository;

    public AmountService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public AmountSumDTO calculateTotalAmountByParentTransaction(Long parentTransactionId) {
        List<Transaction> transactions = this.transactionRepository.findAllByParentId(parentTransactionId);
        if (transactions.isEmpty()) {
            LOGGER.error(String.format("Parent transaction with ID {%s} doesn't exist", parentTransactionId));
            throw new NotFoundException(
                    String.format("Cannot calculate total amount, parent transaction with ID {%s} doesn't exist",
                            parentTransactionId));
        }
        return new AmountSumDTO(transactions.stream().map(Transaction::getAmount)
                .reduce(0.0, Double::sum));
    }
}
