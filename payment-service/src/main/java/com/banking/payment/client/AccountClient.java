package com.banking.payment.client;

import com.banking.payment.dto.AccountResponse;
import com.banking.payment.dto.DebitCreditRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service")
public interface AccountClient {

    @GetMapping("/accounts/{id}")
    AccountResponse getAccountById(@PathVariable("id") Long id);

    @PutMapping("/accounts/{id}/debit")
    AccountResponse debit(@PathVariable("id") Long id, @RequestBody DebitCreditRequest request);

    @PutMapping("/accounts/{id}/credit")
    AccountResponse credit(@PathVariable("id") Long id, @RequestBody DebitCreditRequest request);
}
