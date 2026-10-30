package com.banking.account.service;

import com.banking.account.dto.AccountRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.DebitCreditRequest;
import com.banking.account.dto.TopCustomerDTO;
import com.banking.account.entity.Account;
import com.banking.account.exception.InsufficientBalanceException;
import com.banking.account.repository.AccountRepository;
import com.banking.account.repository.EmployeeRepository;
import com.banking.account.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final PaymentLogRepository paymentLogRepository;

    public AccountResponse createAccount(AccountRequest request) {
        Account account = Account.builder()
                .customerName(request.getCustomerName())
                .balance(request.getBalance())
                .ownerUsername(request.getOwnerUsername())
                .build();
        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public AccountResponse getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found with ID: " + id));
        return mapToResponse(account);
    }

    @Transactional
    public AccountResponse debit(Long id, DebitCreditRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found with ID: " + id));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance in account ID %d. Balance: %s, Requested: %s",
                            id, account.getBalance(), request.getAmount()));
        }

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        Account updated = accountRepository.save(account);
        return mapToResponse(updated);
    }

    @Transactional
    public AccountResponse credit(Long id, DebitCreditRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Account not found with ID: " + id));

        account.setBalance(account.getBalance().add(request.getAmount()));
        Account updated = accountRepository.save(account);
        return mapToResponse(updated);
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new NoSuchElementException("Account not found with ID: " + id);
        }
        accountRepository.deleteById(id);
    }

    public Double getSecondHighestSalary() {
        return employeeRepository.findSecondHighestSalary().orElse(0.0);
    }

    public List<TopCustomerDTO> getTop3Customers() {
        List<Object[]> results = paymentLogRepository.findTop3CustomersByPaymentAmountNative();
        return results.stream().map(row -> new TopCustomerDTO(
                (String) row[0],
                row[1] instanceof BigDecimal ? (BigDecimal) row[1] : new BigDecimal(row[1].toString()),
                ((Number) row[2]).intValue()
        )).collect(Collectors.toList());
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .customerName(account.getCustomerName())
                .balance(account.getBalance())
                .ownerUsername(account.getOwnerUsername())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
