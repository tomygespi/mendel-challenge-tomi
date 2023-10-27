package com.challenge.java.tomi.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.AmountSumDTO;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.service.AmountService;
import com.challenge.java.tomi.service.IAmountService;
import com.challenge.java.tomi.storage.TransactionStorage;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AmountServiceTest {

    private static final Long PARENT_TRANSACTION_ID = 1L;

    private static final Double PARENT_TRANSACTION_AMOUNT = 123.0;

    private static final TypeEnum PARENT_TRANSACTION_TYPE = TypeEnum.CARS;

    private static final Long CHILD_TRANSACTION_ID = 2L;

    private static final Double CHILD_TRANSACTION_AMOUNT = 345.6;

    private static final TypeEnum CHILD_TRANSACTION_TYPE = TypeEnum.SHOPPING;

    private static final Long OTHER_CHILD_TRANSACTION_ID = 2L;

    private static final Double OTHER_CHILD_TRANSACTION_AMOUNT = 345.6;

    private static final TypeEnum OTHER_CHILD_TRANSACTION_TYPE = TypeEnum.FOOD;

    @Mock
    private TransactionStorage transactionStorage;

    private AutoCloseable closeable;

    private IAmountService amountService;

    @BeforeEach
    public void initService() {
        closeable = MockitoAnnotations.openMocks(this);
        amountService =
                new AmountService(this.transactionStorage);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    @Nested
    class calculateTotalAmountByTransactionType {
        @Test
        @SneakyThrows
        public void givenNestedTransactions_whenCalculateTotalAmount_thenReturnSumOfAllAmounts() {
            //given
            Transaction parentTransaction = newParentTransaction();
            Transaction childTransaction = newChildTransaction(parentTransaction);
            Transaction otherChildTransaction = newOtherChildTransaction(parentTransaction);

            //when
            doNothing().when(transactionStorage).save(any(Transaction.class));
            when(transactionStorage.findById(PARENT_TRANSACTION_ID)).thenReturn(parentTransaction);
            when(transactionStorage.findAllByParent(any(Transaction.class)))
                    .thenReturn(new ArrayList<>(List.of(parentTransaction, childTransaction, otherChildTransaction)));

            AmountSumDTO sum = amountService.calculateTotalAmountByParentTransaction(PARENT_TRANSACTION_ID);

            //then
            assertEquals(
                    parentTransaction.getAmount() + childTransaction.getAmount() + otherChildTransaction.getAmount(),
                    sum.getSum());
        }

        @Test
        @SneakyThrows
        public void givenTransactionIdWithoutChildTransaction_whenCalculateTotalAmount_thenReturnSumOfParentAmount() {
            //given
            Transaction parentTransaction = newParentTransaction();

            //when
            when(transactionStorage.findById(PARENT_TRANSACTION_ID)).thenReturn(parentTransaction);
            when(transactionStorage.findAllByParent(any(Transaction.class)))
                    .thenReturn(new ArrayList<>(List.of(parentTransaction)));
            AmountSumDTO sum = amountService.calculateTotalAmountByParentTransaction(PARENT_TRANSACTION_ID);

            //then
            assertEquals(PARENT_TRANSACTION_AMOUNT, sum.getSum());
        }

        @Test
        @SneakyThrows
        public void givenUnsavedTransactionId_whenCalculateTotalAmountByParentId_thenThrowNotFoundException() {
            //given
            //when
            when(transactionStorage.findById(PARENT_TRANSACTION_ID)).thenReturn(null);

            //then
            assertThrows(NotFoundException.class, () -> {
                amountService.calculateTotalAmountByParentTransaction(PARENT_TRANSACTION_ID);
            });
        }
    }

    private Transaction newParentTransaction() {
        Transaction parentTransaction =
                new Transaction(PARENT_TRANSACTION_ID, PARENT_TRANSACTION_AMOUNT, PARENT_TRANSACTION_TYPE);
        return parentTransaction;
    }

    private Transaction newChildTransaction(Transaction parentTransaction) {
        Transaction childTransaction = new Transaction(
                        CHILD_TRANSACTION_ID, CHILD_TRANSACTION_AMOUNT, CHILD_TRANSACTION_TYPE, PARENT_TRANSACTION_ID);
        return childTransaction;
    }

    private Transaction newOtherChildTransaction(Transaction parentTransaction) {
        Transaction childTransaction = new Transaction(
                OTHER_CHILD_TRANSACTION_ID, OTHER_CHILD_TRANSACTION_AMOUNT,
                OTHER_CHILD_TRANSACTION_TYPE, PARENT_TRANSACTION_ID);
        return childTransaction;
    }
}
