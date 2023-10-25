package com.challenge.java.tomi.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;
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

    private Long parentId;
}
