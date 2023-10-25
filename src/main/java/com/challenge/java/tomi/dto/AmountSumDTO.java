package com.challenge.java.tomi.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmountSumDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2034331685950097512L;

    private Double sum;

    public AmountSumDTO(Double sum) {
        this.sum = sum;
    }
}
