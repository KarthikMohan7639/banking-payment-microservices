package com.banking.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for debit and credit requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitCreditRequest {

    @NotNull(message = "Amount is required")
    // WHY: Ensures debit or credit transaction amount is strictly greater than zero.
    @Positive(message = "Amount must be strictly positive")
    private BigDecimal amount;
}
