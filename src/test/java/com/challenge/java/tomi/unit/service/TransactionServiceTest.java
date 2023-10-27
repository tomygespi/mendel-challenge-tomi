package com.challenge.java.tomi.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.TransactionDTO;
import com.challenge.java.tomi.exception.AlreadyExistsException;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.mapper.TransactionMapper;
import com.challenge.java.tomi.mapper.TransactionMapperImpl;
import com.challenge.java.tomi.service.ITransactionService;
import com.challenge.java.tomi.service.TransactionService;
import com.challenge.java.tomi.storage.TransactionStorage;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TransactionServiceTest {

    private static final Long TRANSACTION_ID = 2L;

    private static final Double TRANSACTION_AMOUNT = 123.0;

    private static final TypeEnum TRANSACTION_TYPE = TypeEnum.CARS;

    private static final Long TRANSACTION_PARENT_ID = 1L;

    private static final String UNHANDLED_TRANSACTION_TYPE = "USD";

    private final TransactionMapper transactionMapper = new TransactionMapperImpl();
    @Mock
    private TransactionStorage transactionStorage;

    private AutoCloseable closeable;

    private ITransactionService transactionService;

    @BeforeEach
    public void initService() {
        closeable = MockitoAnnotations.openMocks(this);
        transactionService =
                new TransactionService(this.transactionMapper, this.transactionStorage);
    }

    @AfterEach
    public void closeService() throws Exception {
        closeable.close();
    }

    @Nested
    class createTransactionTests {
        @Test
        @SneakyThrows
        public void givenTransactionDto_whenCreateTransaction_thenSave() {
            //given
            TransactionDTO transactionDTO = newTransactionDTO();

            //when
            doNothing().when(transactionStorage).save(any(Transaction.class));

            TransactionDTO savedTransaction = transactionService.create(TRANSACTION_ID, transactionDTO);

            //then
            verify(transactionStorage, atLeastOnce()).save(any(Transaction.class));
            assertEquals(TRANSACTION_AMOUNT, savedTransaction.getAmount());
            assertEquals(TRANSACTION_TYPE.name(), savedTransaction.getType());
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithSavedParent_whenCreateTransaction_thenSave() {
            //given
            TransactionDTO transactionDTO = newTransactionDTOWithParentId();

            Transaction parentTransaction = new Transaction();
            parentTransaction.setId(TRANSACTION_PARENT_ID);
            parentTransaction.setType(TRANSACTION_TYPE);
            parentTransaction.setAmount(TRANSACTION_AMOUNT);

            //when
            doNothing().when(transactionStorage).save(any(Transaction.class));

            TransactionDTO savedTransaction = transactionService.create(TRANSACTION_ID, transactionDTO);

            //then
            verify(transactionStorage, atLeastOnce()).save(any(Transaction.class));
            assertEquals(TRANSACTION_AMOUNT, savedTransaction.getAmount());
            assertEquals(TRANSACTION_TYPE.name(), savedTransaction.getType());
            assertEquals(TRANSACTION_PARENT_ID, savedTransaction.getParentId());
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithExistingId_whenCreateTransaction_thenThrowAlreadyExistsException() {
            //given
            TransactionDTO transactionDTO = newTransactionDTO();
            Transaction mappedTransaction = newMappedTransactionFromTransactionDTO(transactionDTO);

            //when
            doThrow(EntityExistsException.class).when(transactionStorage).save(any(Transaction.class));

            //then
            assertThrows(AlreadyExistsException.class, () -> {
                transactionService.create(TRANSACTION_ID, transactionDTO);
            });
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithNonExistingParent_whenCreateTransaction_thenThrowNotFoundException() {
            //given
            TransactionDTO transactionDTO = newTransactionDTOWithParentId();

            //when
            doThrow(EntityNotFoundException.class).when(transactionStorage).save(any(Transaction.class));

            //then
            assertThrows(NotFoundException.class, () -> {
                transactionService.create(TRANSACTION_ID, transactionDTO);
            });
        }
    }

    @Nested
    class findTransactionsByTypeTests {
        @Test
        @SneakyThrows
        public void givenTransactionType_whenFindTransactionsIdsByType_thenReturnTransactionsList() {
            //given
            String transactionType = TRANSACTION_TYPE.name();
            List<Transaction> savedTransactions =
                    new ArrayList<>(List.of(newMappedTransactionFromTransactionDTO(newTransactionDTO())));

            //when
            when(transactionStorage.findAllByType(TRANSACTION_TYPE)).thenReturn(savedTransactions);
            List<Long> foundTransactionIds = transactionService.findTransactionIdsByType(transactionType);

            //then
            assertNotNull(foundTransactionIds);
            assertEquals(savedTransactions.size(), foundTransactionIds.size());
            assertTrue(foundTransactionIds.contains(savedTransactions.get(0).getId()));
        }

        @Test
        @SneakyThrows
        public void givenTransactionType_whenFindTransactionsIdsByType_thenReturnEmptyList() {
            //given
            String transactionType = TRANSACTION_TYPE.name();
            //when

            List<Long> foundTransactionIds = transactionService.findTransactionIdsByType(transactionType);

            //then
            assertEquals(0, foundTransactionIds.size());
        }

        @Test
        @SneakyThrows
        public void givenUnhandledTransactionType_whenFindTransactionsIdsByType_thenThrowIllegalArgumentException() {
            //given
            String transactionType = UNHANDLED_TRANSACTION_TYPE;

            //when

            //then
            assertThrows(IllegalArgumentException.class, () -> {
                transactionService.findTransactionIdsByType(transactionType);
            });
        }
    }

    private TransactionDTO newTransactionDTO() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(TRANSACTION_AMOUNT);
        transactionDTO.setType(TRANSACTION_TYPE.name());
        return transactionDTO;
    }

    private TransactionDTO newTransactionDTOWithParentId() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(TRANSACTION_AMOUNT);
        transactionDTO.setType(TRANSACTION_TYPE.name());
        transactionDTO.setParentId(TRANSACTION_PARENT_ID);
        return transactionDTO;
    }

    private Transaction newMappedTransactionFromTransactionDTO(TransactionDTO transactionDTO) {
        Transaction mappedTransaction = this.transactionMapper.toTransaction(transactionDTO);
        mappedTransaction.setId(TRANSACTION_ID);
        return mappedTransaction;
    }

}
