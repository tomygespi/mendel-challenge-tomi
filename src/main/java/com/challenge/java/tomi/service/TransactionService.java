package com.challenge.java.tomi.service;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.TransactionDTO;
import com.challenge.java.tomi.exception.AlreadyExistsException;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.mapper.TransactionMapper;
import com.challenge.java.tomi.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TransactionService implements ITransactionService {

    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionMapper transactionMapper, TransactionRepository transactionRepository) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    @Override
    public Transaction create(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = this.transactionMapper.toTransaction(transactionDTO);
        transaction.setId(id);
        this.validateBeforeCreate(transaction);
        return this.transactionRepository.save(transaction);
    }

    @Transactional
    @Override
    public List<Long> findTransactionIdsByType(String type) {
        this.validateType(type);
        List<Transaction> transactions =
                this.transactionRepository.findTransactionsByType(TypeEnum.valueOf(type.toUpperCase()));
        return transactions
                .stream()
                .map(Transaction::getId)
                .collect(Collectors.toList());
    }

    private void validateBeforeCreate(Transaction transaction) {
        this.validateNotCreated(transaction.getId());

        this.validateAmountNotEmpty(transaction.getAmount());

        this.validateTypeEnum(transaction.getType());

        if (transaction.getParentId() != null) {
            this.validateParent(transaction);
        }
    }

    private void validateNotCreated(Long transactionId) {
        if (this.transactionRepository.findById(transactionId).isPresent()) {
            throw new AlreadyExistsException(
                    String.format("Transaction with ID %s already exists.", transactionId));
        }
    }

    private void validateAmountNotEmpty(Double amount) {
        if (amount == null || amount.isNaN()) {
            throw new IllegalArgumentException("Amount should be a non empty number.");
        }
    }

    private void validateTypeEnum(TypeEnum type) {
        if (type == null) {
            throw new IllegalArgumentException(
                    String.format("Transaction type should be one of the following: %s",
                            Arrays.toString(TypeEnum.values())));
        }
    }

    private void validateParent(Transaction transaction) {
        Transaction parentTransaction = transactionRepository.findTransactionById(transaction.getParentId());
        if (parentTransaction == null) {
            throw new NotFoundException(
                    String.format("Parent transaction with ID %s doesn't exist.", transaction.getParentId()));
        }
        if (transaction.getType() != parentTransaction.getType()) {
            throw new IllegalArgumentException(
                    String.format("Given transaction can't have a different type than it's parent, which is: %s",
                            parentTransaction.getType()));
        }
    }

    private void validateType(String type) {
        if (TypeEnum.find(type.toUpperCase()) == null) {
            throw new IllegalArgumentException(
                    String.format("Transaction type %s not supported, you should use one of the following types: %s",
                    type, Arrays.toString(TypeEnum.values())));
        }
    }
}
