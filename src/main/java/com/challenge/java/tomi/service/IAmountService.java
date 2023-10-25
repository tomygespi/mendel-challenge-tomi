package com.challenge.java.tomi.service;

import com.challenge.java.tomi.dto.AmountSumDTO;

public interface IAmountService {
    AmountSumDTO calculateTotalAmountByParentTransaction(Long parentTransactionId);
}
