package com.banking.account.controller;

import com.banking.account.dto.AccountRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.DebitCreditRequest;
import com.banking.account.dto.TopCustomerDTO;
import com.banking.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody AccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PutMapping("/{id}/debit")
    public ResponseEntity<AccountResponse> debit(@PathVariable Long id, @Valid @RequestBody DebitCreditRequest request) {
        return ResponseEntity.ok(accountService.debit(id, request));
    }

    @PutMapping("/{id}/credit")
    public ResponseEntity<AccountResponse> credit(@PathVariable Long id, @Valid @RequestBody DebitCreditRequest request) {
        return ResponseEntity.ok(accountService.credit(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin/analytics/top-3")
    public ResponseEntity<List<TopCustomerDTO>> getTop3Customers() {
        return ResponseEntity.ok(accountService.getTop3Customers());
    }

    @GetMapping("/admin/analytics/second-highest-salary")
    public ResponseEntity<Double> getSecondHighestSalary() {
        return ResponseEntity.ok(accountService.getSecondHighestSalary());
    }
}
