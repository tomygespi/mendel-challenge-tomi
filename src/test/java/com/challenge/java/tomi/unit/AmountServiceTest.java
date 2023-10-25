package com.challenge.java.tomi.unit;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.AmountSumDTO;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.repository.TransactionRepository;
import com.challenge.java.tomi.service.AmountService;
import com.challenge.java.tomi.service.IAmountService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class AmountServiceTest {

    private static final Long PARENT_TRANSACTION_ID = 1L;

    private static final Double PARENT_TRANSACTION_AMOUNT = 123.0;

    private static final TypeEnum PARENT_TRANSACTION_TYPE = TypeEnum.ARS;

    private static final Long CHILD_TRANSACTION_ID = 2L;

    private static final Double CHILD_TRANSACTION_AMOUNT = 345.6;

    private static final TypeEnum CHILD_TRANSACTION_TYPE = TypeEnum.ARS;

    @Mock
    private TransactionRepository transactionRepository;

    private AutoCloseable closeable;

    private IAmountService amountService;

    @BeforeEach
    public void initService() {
        closeable = MockitoAnnotations.openMocks(this);
        amountService =
                new AmountService(this.transactionRepository);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    @Nested
    class calculateTotalAmountByTransactionType {
        @Test
        @SneakyThrows
        public void givenTransactionIdAndSavedChildTransaction_whenCalculateTotalAmount_thenReturnSumOfBothAmounts() {
            //given
            Transaction parentTransaction = newParentTransaction();
            Transaction childTransaction = newChildTransaction(parentTransaction);

            //when
            when(transactionRepository.findTransactionById(PARENT_TRANSACTION_ID)).thenReturn(parentTransaction);

            AmountSumDTO sum = amountService.calculateTotalAmountByParentTransaction(PARENT_TRANSACTION_ID);

            //then
            assertEquals(PARENT_TRANSACTION_AMOUNT + CHILD_TRANSACTION_AMOUNT, sum.getSum());
        }

        @Test
        @SneakyThrows
        public void givenTransactionIdWithoutChildTransaction_whenCalculateTotalAmount_thenReturnSumOfParentAmount() {
            //given
            Transaction parentTransaction = newParentTransaction();

            //when
            when(transactionRepository.findTransactionById(PARENT_TRANSACTION_ID)).thenReturn(parentTransaction);

            AmountSumDTO sum = amountService.calculateTotalAmountByParentTransaction(PARENT_TRANSACTION_ID);

            //then
            assertEquals(PARENT_TRANSACTION_AMOUNT, sum.getSum());
        }

        @Test
        @SneakyThrows
        public void givenUnsavedTransactionId_whenCalculateTotalAmountByParentId_thenThrowNotFoundException() {
            //given
            //when
            when(transactionRepository.findTransactionById(PARENT_TRANSACTION_ID)).thenReturn(null);

            //then
            assertThrows(NotFoundException.class, () -> {
                amountService.calculateTotalAmountByParentTransaction(PARENT_TRANSACTION_ID);
            });
        }
    }

    private Transaction newParentTransaction() {
        Transaction parentTransaction = new Transaction();
        parentTransaction.setId(PARENT_TRANSACTION_ID);
        parentTransaction.setAmount(PARENT_TRANSACTION_AMOUNT);
        parentTransaction.setType(PARENT_TRANSACTION_TYPE);
        return parentTransaction;
    }

    private Transaction newChildTransaction(Transaction parentTransaction) {
        Transaction childTransaction = new Transaction();
        childTransaction.setId(CHILD_TRANSACTION_ID);
        childTransaction.setAmount(CHILD_TRANSACTION_AMOUNT);
        childTransaction.setType(CHILD_TRANSACTION_TYPE);
        childTransaction.setParentId(parentTransaction.getParentId());
        parentTransaction.setNestedTransactions(new HashSet<>(Set.of(childTransaction)));
        return childTransaction;
    }
}
