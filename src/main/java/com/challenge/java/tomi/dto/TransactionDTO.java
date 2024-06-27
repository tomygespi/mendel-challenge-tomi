package com.challenge.java.tomi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2881709959954799135L;

    @NotNull
    private Double amount;

    @NotBlank
    private String type;

    @JsonProperty("parent_id")
    private Long parentId;

    @Override
    public String toString() {
        return "TransactionDTO{" +
                "amount=" + amount +
                ", type='" + type + '\'' +
                ", parentId=" + parentId +
                '}';
    }
}
