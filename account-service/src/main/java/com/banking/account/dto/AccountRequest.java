package com.banking.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Data Transfer Object for account creation requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequest {

    // WHY: Validates string input is neither null nor blank whitespace.
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Initial balance is required")
    // WHY: Ensures monetary balance cannot be negative upon creation.
    @PositiveOrZero(message = "Initial balance must be zero or positive")
    private BigDecimal balance;

    // WHY: Enforces non-blank owner username.
    @NotBlank(message = "Owner username is required")
    private String ownerUsername;
}
