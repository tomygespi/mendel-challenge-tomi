package com.challenge.java.tomi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.challenge.java.tomi.Application;
import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.TransactionDTO;
import com.challenge.java.tomi.exception.NotFoundException;
import com.challenge.java.tomi.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    private static final String CREATE_TRANSACTION_ENDPOINT = "/transactions/%s";

    private static final String FIND_ALL_TRANSACTION_IDS_BY_TYPE_ENDPOINT = "/transactions/types/%s";

    private static final String GET_TOTAL_AMOUNT_BY_PARENT_TRANSACTION_ID_ENDPOINT = "/transactions/sum/%s";

    private static final Long TRANSACTION_ID = 2L;

    private static final Double TRANSACTION_AMOUNT = 123.0;

    private static final TypeEnum TRANSACTION_TYPE = TypeEnum.CARS;

    private static final Long OTHER_TRANSACTION_ID = 3L;

    private static final TypeEnum OTHER_TRANSACTION_TYPE = TypeEnum.SHOPPING;

    private static final Double OTHER_TRANSACTION_AMOUNT = 100.0;

    private static final Long TRANSACTION_PARENT_ID = 1L;

    private static final Double TRANSACTION_PARENT_AMOUNT = 456.7;

    private static final TypeEnum TRANSACTION_PARENT_TYPE = TypeEnum.CARS;

    private static final String UNHANDLED_TRANSACTION_TYPE = "HOTEL";

    private static final String AMOUNT_ERROR_MESSAGE = "Amount should be a non empty number.";

    private static final String EXISTING_TRANSACTION_ERROR_MESSAGE = "Transaction with ID %s already exists.";

    private static final String VALIDATION_ERROR_MESSAGE = "Error when creating transaction, please check given fields";

    private static final String UNSAVED_PARENT_TRANSACTION_ERROR_MESSAGE =
            "Error when creating transaction, parent transaction ID {%s} does not exist";

    private static final String NOT_SUPPORTED_TYPE_ERROR_MESSAGE =
            "Transaction type %s not supported, you should use one of the following types: %s";

    private static final String DIFFERENT_TRANSACTION_TYPES_ERROR_MESSAGE =
            "Given transaction can't have a different type than it's parent, which is: %s";

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void clean() {
        transactionRepository.deleteAll();
    }

    @Nested
    class CreateTransactionTests {
        @Test
        @SneakyThrows
        public void givenTransactionDTO_whenCreate_thenReturnTransaction() {
            //given
            TransactionDTO transactionDTO = newTransactionDTO();

            //when
            mockMvc
                    .perform(put(String.format(CREATE_TRANSACTION_ENDPOINT, TRANSACTION_ID))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(transactionDTO)))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.type").value(TRANSACTION_TYPE.name()))
                    .andExpect(jsonPath("$.amount").value(TRANSACTION_AMOUNT));

            Transaction savedTransaction = transactionRepository.findById(TRANSACTION_ID);
            assertNotNull(savedTransaction);
            assertEquals(TRANSACTION_ID, savedTransaction.getId());
            assertEquals(TRANSACTION_TYPE, savedTransaction.getType());
            assertEquals(TRANSACTION_AMOUNT, savedTransaction.getAmount());
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithSavedParent_whenCreate_thenReturnTransaction() {
            //given
            Transaction parentTransaction = newParentTransaction();
            transactionRepository.save(parentTransaction);
            TransactionDTO transactionDTO = newTransactionDTO();
            transactionDTO.setParentId(parentTransaction.getId());

            //when
            mockMvc
                    .perform(put(String.format(CREATE_TRANSACTION_ENDPOINT, TRANSACTION_ID))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(transactionDTO)))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.type").value(TRANSACTION_TYPE.name()))
                    .andExpect(jsonPath("$.amount").value(TRANSACTION_AMOUNT))
                    .andExpect(jsonPath("$.parent_id").value(TRANSACTION_PARENT_ID));

            Transaction savedTransaction = transactionRepository.findById(TRANSACTION_ID);

            assertNotNull(savedTransaction);
            assertEquals(TRANSACTION_ID, savedTransaction.getId());
            assertEquals(TRANSACTION_TYPE, savedTransaction.getType());
            assertEquals(TRANSACTION_AMOUNT, savedTransaction.getAmount());
            assertEquals(TRANSACTION_PARENT_ID, savedTransaction.getParentId());
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithExistingId_whenCreate_thenReturnIdempotencyOk() {
            //given
            transactionRepository.save(newTransaction());
            TransactionDTO transactionDTO = newTransactionDTO();

            //when
            mockMvc
                    .perform(put(String.format(CREATE_TRANSACTION_ENDPOINT, TRANSACTION_ID))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(transactionDTO)))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
        }

        @Test
        @SneakyThrows
        public void givenTransactionDTOWithoutAmount_whenCreate_thenReturnBadRequestWithIllegalArgumentException() {
            //given
            TransactionDTO transactionDTO = newTransactionDTO();
            transactionDTO.setAmount(null);

            //when
            MvcResult response =
                    mockMvc
                            .perform(put(String.format(CREATE_TRANSACTION_ENDPOINT, TRANSACTION_ID))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsBytes(transactionDTO)))
                            //then
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andReturn();

            assertEquals(IllegalArgumentException.class, response.getResolvedException().getClass());
        }

        @Test
        @SneakyThrows
        public void givenTransactionWithUnhandledType_whenCreate_thenReturnBadRequestWithIllegalArgumentException() {
            //given
            TransactionDTO transactionDTO = newTransactionDTO();
            transactionDTO.setType(UNHANDLED_TRANSACTION_TYPE);

            //when
            MvcResult response =
                    mockMvc
                            .perform(put(String.format(CREATE_TRANSACTION_ENDPOINT, TRANSACTION_ID))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsBytes(transactionDTO)))
                            //then
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andReturn();

            assertEquals(IllegalArgumentException.class, response.getResolvedException().getClass());
            assertEquals(String.format(VALIDATION_ERROR_MESSAGE, Arrays.toString(TypeEnum.values())),
                    response.getResolvedException().getMessage());
        }

        @Test
        @SneakyThrows
        public void givenTransactionWithUnsavedParent_whenCreate_thenReturnNotFoundWithNotFoundException() {
            //given
            TransactionDTO transactionDTO = newTransactionDTO();
            transactionDTO.setParentId(-99L);

            //when
            MvcResult response =
                    mockMvc
                            .perform(put(String.format(CREATE_TRANSACTION_ENDPOINT, TRANSACTION_ID))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsBytes(transactionDTO)))
                            //then
                            .andDo(print())
                            .andExpect(status().isNotFound())
                            .andReturn();

            assertEquals(NotFoundException.class, response.getResolvedException().getClass());
            assertEquals(String.format(UNSAVED_PARENT_TRANSACTION_ERROR_MESSAGE, transactionDTO.getParentId()),
                    response.getResolvedException().getMessage());
        }

        @Test
        @SneakyThrows
        public void givenTransactionOtherTypeThanParent_whenCreate_thenReturnTransaction() {
            //given
            Transaction parentTransaction = newParentTransaction();
            transactionRepository.save(parentTransaction);
            TransactionDTO transactionDTO = newTransactionDTO();
            transactionDTO.setParentId(parentTransaction.getId());
            transactionDTO.setType(OTHER_TRANSACTION_TYPE.name());

            //when
            mockMvc
                    .perform(put(String.format(CREATE_TRANSACTION_ENDPOINT, TRANSACTION_ID))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(transactionDTO)))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.type").value(OTHER_TRANSACTION_TYPE.name()))
                    .andExpect(jsonPath("$.amount").value(TRANSACTION_AMOUNT))
                    .andExpect(jsonPath("$.parent_id").value(TRANSACTION_PARENT_ID));

            Transaction savedTransaction = transactionRepository.findById(TRANSACTION_ID);
            Transaction savedParentTransaction = transactionRepository.findById(TRANSACTION_PARENT_ID);

            assertNotNull(savedTransaction);
            assertNotNull(parentTransaction);
            assertEquals(TRANSACTION_ID, savedTransaction.getId());
            assertEquals(TRANSACTION_PARENT_ID, savedParentTransaction.getId());
            assertNotEquals(savedParentTransaction.getId(), savedTransaction.getId());
        }
    }

    @Nested
    class findAllIdsByTypeTests{
        @Test
        @SneakyThrows
        public void givenTransactionType_whenFindTransactionsByType_thenReturnTransactionIdsList() {
            //given
            Transaction parentTransaction = newParentTransaction();
            transactionRepository.save(parentTransaction);
            Transaction childTransaction = newTransaction();
            childTransaction.setParentId(parentTransaction.getId());
            transactionRepository.save(childTransaction);
            Transaction otherTransactionWithOtherType = newOtherTransaction();
            transactionRepository.save(otherTransactionWithOtherType);
            String type = childTransaction.getType().name();

            //when
            mockMvc
                    .perform(get(String.format(FIND_ALL_TRANSACTION_IDS_BY_TYPE_ENDPOINT, type))
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$.[0]").value(parentTransaction.getId()))
                    .andExpect(jsonPath("$.[1]").value(childTransaction.getId()));
        }

        @Test
        @SneakyThrows
        public void givenUnhandledTransactionType_whenFindTransactionsByType_thenReturnBadRequestWithIllegalArgument() {
            //given
            Transaction parentTransaction = newParentTransaction();
            transactionRepository.save(parentTransaction);
            Transaction childTransaction = newTransaction();
            childTransaction.setParentId(parentTransaction.getId());
            transactionRepository.save(childTransaction);

            //when
            MvcResult response =
                    mockMvc
                            .perform(get(String.format(
                                    FIND_ALL_TRANSACTION_IDS_BY_TYPE_ENDPOINT, UNHANDLED_TRANSACTION_TYPE))
                                    .contentType(MediaType.APPLICATION_JSON))
                            //then
                            .andDo(print())
                            .andExpect(status().isBadRequest())
                            .andReturn();

            assertEquals(IllegalArgumentException.class, response.getResolvedException().getClass());
        }
    }

    @Nested
    class calculateAmountSumTests {
        @Test
        @SneakyThrows
        public void givenParentIdWithNestedTransaction_whenGetTotalAmountByParentTransaction_thenReturnSumOfBoth() {
            //given
            Transaction parentTransaction = newParentTransaction();
            Transaction childTransaction = newTransaction();
            childTransaction.setParentId(parentTransaction.getId());
            transactionRepository.save(parentTransaction);
            transactionRepository.save(childTransaction);

            //when
            mockMvc
                    .perform(get(String.format(
                            GET_TOTAL_AMOUNT_BY_PARENT_TRANSACTION_ID_ENDPOINT, parentTransaction.getId()))
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sum")
                            .value(TRANSACTION_AMOUNT + TRANSACTION_PARENT_AMOUNT));
        }

        @Test
        @SneakyThrows
        public void givenParentIdWithoutNestedTransactions_whenGetTotalAmountByParentId_thenReturnParentAmount() {
            //given
            Transaction parentTransaction = newParentTransaction();
            transactionRepository.save(parentTransaction);

            //when
            mockMvc
                    .perform(get(String.format(
                            GET_TOTAL_AMOUNT_BY_PARENT_TRANSACTION_ID_ENDPOINT, parentTransaction.getId()))
                            .contentType(MediaType.APPLICATION_JSON))
                    //then
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sum")
                            .value(TRANSACTION_PARENT_AMOUNT));
        }

        @Test
        @SneakyThrows
        public void givenUnsavedParentTransactionId_whenGetTotalAmountByParentId_thenReturnNotFoundWithNotFoundException() {
            //given
            //when
            MvcResult response =
                    mockMvc
                            .perform(get(String.format(
                            GET_TOTAL_AMOUNT_BY_PARENT_TRANSACTION_ID_ENDPOINT, TRANSACTION_ID))
                                    .contentType(MediaType.APPLICATION_JSON))
                            //then
                            .andDo(print())
                            .andExpect(status().isNotFound())
                            .andReturn();

            assertEquals(NotFoundException.class, response.getResolvedException().getClass());
            assertEquals(String.format("Cannot calculate total amount, parent transaction with ID {%s} doesn't exist",
                            TRANSACTION_ID), response.getResolvedException().getMessage());
        }
    }

    private TransactionDTO newTransactionDTO() {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(TRANSACTION_AMOUNT);
        transactionDTO.setType(TRANSACTION_TYPE.name());
        return transactionDTO;
    }

    private Transaction newTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);
        transaction.setAmount(TRANSACTION_AMOUNT);
        transaction.setType(TRANSACTION_TYPE);
        return transaction;
    }

    private Transaction newOtherTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(OTHER_TRANSACTION_ID);
        transaction.setAmount(OTHER_TRANSACTION_AMOUNT);
        transaction.setType(OTHER_TRANSACTION_TYPE);
        return transaction;
    }

    private Transaction newParentTransaction() {
        Transaction parentTransaction = new Transaction();
        parentTransaction.setId(TRANSACTION_PARENT_ID);
        parentTransaction.setAmount(TRANSACTION_PARENT_AMOUNT);
        parentTransaction.setType(TRANSACTION_PARENT_TYPE);
        return parentTransaction;
    }
}
