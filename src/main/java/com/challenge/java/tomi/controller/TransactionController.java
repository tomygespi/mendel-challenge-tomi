package com.challenge.java.tomi.controller;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.dto.AmountSumDTO;
import com.challenge.java.tomi.dto.TransactionDTO;
import com.challenge.java.tomi.service.IAmountService;
import com.challenge.java.tomi.service.ITransactionService;
import com.challenge.java.tomi.service.TransactionService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TransactionController {
    private final IAmountService amountService;
    private final ITransactionService transactionService;

    public TransactionController(IAmountService amountService, TransactionService transactionService) {
        this.amountService = amountService;
        this.transactionService = transactionService;
    }

    @PutMapping("/transactions/{transactionId}")
    public ResponseEntity<Transaction> create(
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(this.transactionService.create(transactionId, transactionDTO));
    }

    @GetMapping("/transactions/sum/{transactionId}")
    public ResponseEntity<AmountSumDTO> getTotalAmountByParentTransaction(
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(this.amountService.calculateTotalAmountByParentTransaction(transactionId));
    }

    @GetMapping("/transactions/types/{type}")
    public ResponseEntity<List<Long>> getAllIdsByType(
            @PathVariable String type) {
        return ResponseEntity.ok(this.transactionService.findTransactionIdsByType(type));
    }
}
