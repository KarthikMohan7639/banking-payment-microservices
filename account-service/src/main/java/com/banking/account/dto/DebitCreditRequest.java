package com.banking.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitCreditRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be strictly positive")
    private BigDecimal amount;
}
