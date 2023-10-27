package com.challenge.java.tomi.service;

import com.challenge.java.tomi.dto.TransactionDTO;
import java.util.List;

public interface ITransactionService {
    TransactionDTO create(Long id, TransactionDTO transactionDTO);

    List<Long> findTransactionIdsByType(String type);
}
