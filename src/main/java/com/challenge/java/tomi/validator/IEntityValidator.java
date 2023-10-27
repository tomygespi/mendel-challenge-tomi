package com.challenge.java.tomi.validator;

import com.challenge.java.tomi.domain.Transaction;
import java.util.Map;

public interface IEntityValidator {
    void validate(Map<Long, Transaction> storage, Transaction transaction);
}
