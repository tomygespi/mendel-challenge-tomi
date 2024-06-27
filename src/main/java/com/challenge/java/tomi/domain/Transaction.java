package com.challenge.java.tomi.domain;

import com.challenge.java.tomi.domain.transaction.TypeEnum;
import com.challenge.java.tomi.dto.TransactionDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Arrays;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    private Long id;

    @NotNull
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    private Long parentId;

    public Transaction() {}

    public Transaction(Long id, TransactionDTO transactionDTO) {
        this.validateAmount(transactionDTO.getAmount());
        this.validateType(transactionDTO.getType());
        this.id = id;
        this.amount = transactionDTO.getAmount();
        this.type = TypeEnum.find(transactionDTO.getType().toUpperCase());
        this.parentId = transactionDTO.getParentId();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", type=" + type +
                ", parentId=" + parentId +
                '}';
    }

    private void validateAmount(Double amount) {
        if (amount == null || amount.isNaN()) throw new ValidationException("Amount should be a non empty number.");
    }

    private void validateType(String type) {
        if (type == null || TypeEnum.find(type.toUpperCase()) == null) {
            throw new ValidationException(String.format("Transaction type should be one of the following: {%s}",
                    Arrays.toString(TypeEnum.values())));
        }
    }
}
