package com.challenge.java.tomi.service;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.TransactionDTO;
import com.challenge.java.tomi.exception.AlreadyExistsException;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.mapper.TransactionMapper;
import com.challenge.java.tomi.storage.TransactionStorage;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class TransactionService implements ITransactionService {

    private final TransactionMapper transactionMapper;
    private final TransactionStorage transactionStorage;

    public TransactionService(TransactionMapper transactionMapper, TransactionStorage transactionStorage) {
        this.transactionMapper = transactionMapper;
        this.transactionStorage = transactionStorage;
    }

    @Transactional
    @Override
    public TransactionDTO create(Long id, TransactionDTO transactionDTO) {
        try {
            Transaction transaction = this.transactionMapper.toTransaction(transactionDTO);
            transaction.setId(id);
            this.transactionStorage.save(transaction);
            return transactionDTO;
        } catch (EntityExistsException e) {
            throw new AlreadyExistsException(e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        catch (ValidationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    @Override
    public List<Long> findTransactionIdsByType(String type) {
        this.validateType(type);
        return this.transactionStorage.findAllByType(TypeEnum.valueOf(type.toUpperCase()))
                .stream()
                .map(Transaction::getId)
                .collect(Collectors.toList());
    }

    private void validateType(String type) {
        if (TypeEnum.find(type.toUpperCase()) == null) {
            throw new IllegalArgumentException(
                    String.format("Transaction type %s not supported, you should use one of the following types: %s",
                    type, Arrays.toString(TypeEnum.values())));
        }
    }
}
