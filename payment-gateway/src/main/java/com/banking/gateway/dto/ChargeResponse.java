package com.banking.gateway.dto;

import com.banking.gateway.entity.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChargeResponse {
    private String gatewayRef;
    private TransactionStatus status;
}
