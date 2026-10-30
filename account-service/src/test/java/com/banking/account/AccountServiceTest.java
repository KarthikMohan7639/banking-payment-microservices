package com.banking.account;

import com.banking.account.dto.AccountRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.DebitCreditRequest;
import com.banking.account.entity.Account;
import com.banking.account.exception.InsufficientBalanceException;
import com.banking.account.repository.AccountRepository;
import com.banking.account.repository.EmployeeRepository;
import com.banking.account.repository.PaymentLogRepository;
import com.banking.account.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PaymentLogRepository paymentLogRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .accountId(1L)
                .customerName("John Doe")
                .balance(new BigDecimal("1000.00"))
                .ownerUsername("john_doe")
                .build();
    }

    @Test
    void testCreateAccount() {
        AccountRequest request = new AccountRequest("John Doe", new BigDecimal("1000.00"), "john_doe");
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        AccountResponse response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("John Doe", response.getCustomerName());
        assertEquals(new BigDecimal("1000.00"), response.getBalance());
    }

    @Test
    void testDebitSuccess() {
        DebitCreditRequest request = new DebitCreditRequest(new BigDecimal("200.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.debit(1L, request);

        assertEquals(new BigDecimal("800.00"), response.getBalance());
        verify(accountRepository).save(testAccount);
    }

    @Test
    void testDebitInsufficientBalanceThrowsException() {
        DebitCreditRequest request = new DebitCreditRequest(new BigDecimal("1500.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        assertThrows(InsufficientBalanceException.class, () -> accountService.debit(1L, request));
    }

    @Test
    void testCreditSuccess() {
        DebitCreditRequest request = new DebitCreditRequest(new BigDecimal("500.00"));
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AccountResponse response = accountService.credit(1L, request);

        assertEquals(new BigDecimal("1500.00"), response.getBalance());
    }
}
