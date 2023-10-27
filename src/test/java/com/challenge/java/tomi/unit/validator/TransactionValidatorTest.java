package com.challenge.java.tomi.unit.validator;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.validator.IEntityValidator;
import com.challenge.java.tomi.validator.TransactionValidator;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionValidatorTest {

    private Map<Long, Transaction> MOCKED_TRANSACTIONS = new ConcurrentHashMap<>();

    private static final Long TRANSACTION_ID = 1L;

    private static final Double TRANSACTION_AMOUNT = 123.0;

    private static final TypeEnum TRANSACTION_TYPE = TypeEnum.CARS;

    private IEntityValidator entityValidator;

    @BeforeEach
    public void setup() {
        entityValidator = new TransactionValidator();
    }

    @Test
    @SneakyThrows
    public void givenTransactionWithSavedId_whenValidate_thenThrowEntityExistsException() {
        //given
        Transaction transaction = this.newTransaction();
        MOCKED_TRANSACTIONS.put(transaction.getId(), transaction);

        //when
        //then
        assertThrows(EntityExistsException.class, () -> {
            entityValidator.validate(MOCKED_TRANSACTIONS, transaction);
        });
    }

    @Test
    @SneakyThrows
    public void givenTransactionWithUnsavedParentId_whenValidate_thenThrowEntityNotFoundException() {
        //given
        Transaction transaction = this.newTransaction();
        transaction.setParentId(-99L);

        //when
        //then
        assertThrows(EntityNotFoundException.class, () -> {
            entityValidator.validate(MOCKED_TRANSACTIONS, transaction);
        });
    }

    @Test
    @SneakyThrows
    public void givenTransactionEmptyAmount_whenValidate_thenThrowValidationException() {
        //given
        Transaction transaction = this.newTransaction();
        transaction.setAmount(null);

        //when
        //then
        assertThrows(ValidationException.class, () -> {
            entityValidator.validate(MOCKED_TRANSACTIONS, transaction);
        });
    }

    @Test
    @SneakyThrows
    public void givenTransactionWitEmptyType_whenValidate_thenThrowValidationException() {
        //given
        Transaction transaction = this.newTransaction();
        transaction.setType(null);

        //when
        //then
        assertThrows(ValidationException.class, () -> {
            entityValidator.validate(MOCKED_TRANSACTIONS, transaction);
        });
    }

    private Transaction newTransaction() {
        Transaction parentTransaction = new Transaction();
        parentTransaction.setId(TRANSACTION_ID);
        parentTransaction.setAmount(TRANSACTION_AMOUNT);
        parentTransaction.setType(TRANSACTION_TYPE);
        return parentTransaction;
    }
}
