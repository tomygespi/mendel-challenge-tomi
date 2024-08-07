package com.challenge.java.tomi.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.TransactionDTO;
import com.challenge.java.tomi.repository.TransactionRepository;
import com.challenge.java.tomi.service.ITransactionService;
import com.challenge.java.tomi.service.TransactionService;
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

    @Mock
    private TransactionRepository transactionRepository;

    private AutoCloseable closeable;

    private ITransactionService transactionService;

    @BeforeEach
    public void initService() {
        closeable = MockitoAnnotations.openMocks(this);
        transactionService =
                new TransactionService(this.transactionRepository);
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
            Transaction mappedTransaction = newMappedTransactionFromTransactionDTO(transactionDTO);

            //when
            when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(null);
            doNothing().when(transactionRepository).save(any(Transaction.class));
            when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(mappedTransaction);

            transactionService.create(TRANSACTION_ID, transactionDTO);
            Transaction savedTransaction = transactionRepository.findById(TRANSACTION_ID);

            //then
            verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
            assertEquals(TRANSACTION_ID, savedTransaction.getId());
            assertEquals(TRANSACTION_AMOUNT, savedTransaction.getAmount());
            assertEquals(TRANSACTION_TYPE, savedTransaction.getType());
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithSavedParent_whenCreateTransaction_thenSave() {
            //given
            TransactionDTO transactionDTO = newTransactionDTOWithParentId();
            Transaction mappedTransaction = newMappedTransactionFromTransactionDTO(transactionDTO);

            Transaction parentTransaction = new Transaction();
            parentTransaction.setId(TRANSACTION_PARENT_ID);
            parentTransaction.setType(TRANSACTION_TYPE);
            parentTransaction.setAmount(TRANSACTION_AMOUNT);

            //when
            when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(null);
            when(transactionRepository.findById(TRANSACTION_PARENT_ID)).thenReturn(parentTransaction);

            doNothing().when(transactionRepository).save(any(Transaction.class));
            when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(mappedTransaction);

            transactionService.create(TRANSACTION_ID, transactionDTO);
            Transaction savedTransaction = transactionRepository.findById(TRANSACTION_ID);

            //then
            verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
            assertEquals(TRANSACTION_ID, savedTransaction.getId());
            assertEquals(TRANSACTION_AMOUNT, savedTransaction.getAmount());
            assertEquals(TRANSACTION_TYPE, savedTransaction.getType());
            assertEquals(TRANSACTION_PARENT_ID, savedTransaction.getParentId());
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithoutAmount_whenCreateTransaction_thenThrowIllegalArgumentException() {
            //given
            TransactionDTO transactionDTO = newTransactionDTO();
            transactionDTO.setAmount(null);

            //when
            when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(null);

            //then
            assertThrows(IllegalArgumentException.class, () -> {
                transactionService.create(TRANSACTION_ID, transactionDTO);
            });
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithUnhandledType_whenCreateTransaction_thenThrowIllegalArgumentException() {
            //given
            TransactionDTO transactionDTO = newTransactionDTO();
            transactionDTO.setType(UNHANDLED_TRANSACTION_TYPE);

            //when
            when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(null);

            //then
            assertThrows(IllegalArgumentException.class, () -> {
                transactionService.create(TRANSACTION_ID, transactionDTO);
            });
        }

        @Test
        @SneakyThrows
        public void givenTransactionWithTypeDifferentThanParent_whenCreateTransaction_thenThrowIllegalArgumentException() {
            //given
            TransactionDTO transactionDTO = newTransactionDTOWithParentId();
            transactionDTO.setType("MXN");

            Transaction parentTransaction = new Transaction();
            parentTransaction.setId(TRANSACTION_PARENT_ID);
            parentTransaction.setType(TRANSACTION_TYPE);
            parentTransaction.setAmount(TRANSACTION_AMOUNT);

            //when
            when(transactionRepository.findById(TRANSACTION_ID)).thenReturn(null);
            when(transactionRepository.findById(TRANSACTION_PARENT_ID)).thenReturn(parentTransaction);

            //then
            assertThrows(IllegalArgumentException.class, () -> {
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
            when(transactionRepository.findAllByType(TRANSACTION_TYPE)).thenReturn(savedTransactions);

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
            when(transactionRepository.findAllByType(TRANSACTION_TYPE)).thenReturn(new ArrayList<>());

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
        return new Transaction(TRANSACTION_ID, transactionDTO);
    }

}
