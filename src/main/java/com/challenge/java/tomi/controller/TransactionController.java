package com.challenge.java.tomi.controller;

import com.challenge.java.tomi.domain.Transaction;
import com.challenge.java.tomi.dto.AmountSumDTO;
import com.challenge.java.tomi.dto.TransactionDTO;
import com.challenge.java.tomi.service.IAmountService;
import com.challenge.java.tomi.service.ITransactionService;
import com.challenge.java.tomi.service.TransactionService;
import java.util.List;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
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

    @PutMapping(value = "/transactions/{transactionId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new transaction from given ID and DTO")
    @ApiResponse(
            responseCode = "200",
            description = "Transaction created",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Transaction.class))}
    )
    @ApiResponse(
            responseCode = "400",
            description = "Bad request from transaction's wrong argument",
            content = @Content
    )
    @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content
    )
    @ApiResponse(
            responseCode = "409",
            description = "Transaction already exists for given transaction ID",
            content = @Content
    )
    public ResponseEntity<Transaction> create(
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(this.transactionService.create(transactionId, transactionDTO));
    }

    @GetMapping(value = "/transactions/sum/{transactionId}",
    produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get nested transactions total amount by parent transaction ID")
    @ApiResponse(
            responseCode = "200",
            description = "Total amount sum",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AmountSumDTO.class))}
    )
    @ApiResponse(
            responseCode = "404",
            description = "Transaction not found",
            content = @Content
    )
    public ResponseEntity<AmountSumDTO> getTotalAmountByParentTransaction(
            @PathVariable Long transactionId) {
        return ResponseEntity.ok(this.amountService.calculateTotalAmountByParentTransaction(transactionId));
    }

    @GetMapping(value = "/transactions/types/{type}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all the transactions IDs by transaction type")
    @ApiResponse(
            responseCode = "200",
            description = "Total amount sum",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = List.class))}
    )
    @ApiResponse(
            responseCode = "400",
            description = "Type not supported",
            content = @Content
    )
    public ResponseEntity<List<Long>> getAllIdsByType(
            @PathVariable String type) {
        return ResponseEntity.ok(this.transactionService.findTransactionIdsByType(type));
    }
}
