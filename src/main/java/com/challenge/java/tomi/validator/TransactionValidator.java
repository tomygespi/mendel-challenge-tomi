package com.challenge.java.tomi.validator;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.exception.TransactionExistsException;
import com.challenge.java.tomi.exception.TransactionNotFoundException;
import java.util.Map;
import javax.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator implements ITransactionValidator {
    @Override
    public void validateBeforeCreate(Map<Long, Transaction> transactionStorage, Transaction transaction) {
        if (transaction.getParentId() != null) this.validateParent(transactionStorage, transaction);
        this.validateNotCreated(transactionStorage, transaction);
    }

    @Override
    public void validateType(TypeEnum type) {
        if (type == null) {
            throw new ValidationException("Transaction type cannot be null");
        }
    }

    private void validateNotCreated(Map<Long, Transaction> transactionStorage, Transaction transaction) {
        if (transactionStorage.containsKey(transaction.getId())) {
            throw new TransactionExistsException(
                    String.format("Transaction with ID %s already exists.", transaction.getId()));
        }
    }

    private void validateParent(Map<Long, Transaction> transactionStorage, Transaction transaction) {
        if (!transactionStorage.containsKey(transaction.getParentId())) {
            throw new TransactionNotFoundException(
                    String.format("Parent transaction with ID %s doesn't exist.", transaction.getParentId()));
        }
    }
}
