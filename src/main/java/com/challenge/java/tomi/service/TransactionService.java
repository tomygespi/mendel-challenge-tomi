package com.challenge.java.tomi.service;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.TransactionDTO;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.exception.TransactionExistsException;
import com.challenge.java.tomi.exception.TransactionNotFoundException;
import com.challenge.java.tomi.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionService implements ITransactionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public TransactionDTO create(Long id, TransactionDTO transactionDTO) {
        try {
            Transaction transaction = new Transaction(id, transactionDTO);
            this.transactionRepository.save(transaction);
            return transactionDTO;
        } catch (TransactionExistsException e) {
            LOGGER.warn(String.format("Transaction with ID {%s} already exists", id));
            return transactionDTO;
        } catch (TransactionNotFoundException e) {
            LOGGER.error(String.format(
                    "Could not save transaction {%s} because parent transaction with ID {%s} doesnt exist",
                    id, transactionDTO.getParentId()));
            throw new NotFoundException(
                    String.format("Error when creating transaction, parent transaction ID {%s} does not exist",
                            transactionDTO.getParentId()), e);
        } catch (ValidationException e) {
            LOGGER.error(String.format("Validation error on given fields for transaction {%s}", transactionDTO));
            throw new IllegalArgumentException("Error when creating transaction, please check given fields", e);
        }
    }

    @Override
    public List<Long> findTransactionIdsByType(String type) {
        try {
            List<Transaction> transactions =
                    this.transactionRepository.findAllByType(TypeEnum.valueOf(type.toUpperCase()));
            return transactions
                    .stream()
                    .map(Transaction::getId)
                    .collect(Collectors.toList());
        } catch (ValidationException e) {
            LOGGER.error(String.format("Validation error on given transaction type {%s}", type));
            throw new IllegalArgumentException(String.format("Transaction type should be one of the following: %s",
                    Arrays.toString(TypeEnum.values())));
        }
    }
}
