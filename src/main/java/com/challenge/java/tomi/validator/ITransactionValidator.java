package com.challenge.java.tomi.validator;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.domain.transaction.TypeEnum;
import java.util.Map;

public interface ITransactionValidator {
    void validateBeforeCreate(Map<Long, Transaction> transactionStorage, Transaction transaction);

    void validateType(TypeEnum transactionType);
}
