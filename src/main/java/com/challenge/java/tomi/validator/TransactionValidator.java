package com.challenge.java.tomi.validator;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator implements IEntityValidator {
    @Override
    public void validate(Map<Long, Transaction> storage, Transaction transaction) {
        this.validateNotCreated(storage, transaction.getId());

        this.validateAmountNotEmpty(transaction.getAmount());

        this.validateTypeEnum(transaction.getType());

        if (transaction.getParentId() != null) {
            this.validateParent(storage, transaction);
        }
    }

    private void validateNotCreated(Map<Long, Transaction> storage, Long id) {
        if (storage.containsKey(id)) {
            throw new EntityExistsException(String.format("Transaction with ID %s already exists.", id));
        }
    }

    private void validateAmountNotEmpty(Double amount) {
        if (amount == null || amount.isNaN()) {
            throw new ValidationException("Amount should be a non empty number.");
        }
    }

    private void validateTypeEnum(TypeEnum type) {
        if (type == null) {
            throw new ValidationException(
                    String.format("Transaction type should be one of the following: %s",
                            Arrays.toString(TypeEnum.values())));
        }
    }

    private void validateParent(Map<Long, Transaction> storage, Transaction transaction) {
        Transaction parentTransaction = storage.get(transaction.getParentId());
        if (parentTransaction == null) {
            throw new EntityNotFoundException(
                    String.format("Parent transaction with ID %s doesn't exist.", transaction.getParentId()));
        }
    }
}
