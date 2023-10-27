package com.challenge.java.tomi.domain;

import com.challenge.java.tomi.domain.transaction.TypeEnum;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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

    public Transaction() {
    }

    public Transaction(Long id, Double amount, TypeEnum type) {
        this.id = id;
        this.amount = amount;
        this.type = type;
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

    public Transaction(Long id, Double amount, TypeEnum type, Long parentId) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.parentId = parentId;


    }
}
