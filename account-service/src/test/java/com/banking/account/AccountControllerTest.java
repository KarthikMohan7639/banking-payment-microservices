package com.banking.account;

import com.banking.account.controller.AccountController;
import com.banking.account.dto.AccountRequest;
import com.banking.account.dto.AccountResponse;
import com.banking.account.dto.DebitCreditRequest;
import com.banking.account.exception.GlobalExceptionHandler;
import com.banking.account.exception.InsufficientBalanceException;
import com.banking.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@ContextConfiguration(classes = {AccountController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateAccountValidationFailure() throws Exception {
        AccountRequest invalidRequest = new AccountRequest("", new BigDecimal("-100"), "");

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.customerName").exists())
                .andExpect(jsonPath("$.errors.balance").exists());
    }

    @Test
    void testDebitInsufficientBalance() throws Exception {
        DebitCreditRequest debitRequest = new DebitCreditRequest(new BigDecimal("5000.00"));
        when(accountService.debit(eq(1L), any(DebitCreditRequest.class)))
                .thenThrow(new InsufficientBalanceException("Insufficient balance"));

        mockMvc.perform(put("/accounts/1/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debitRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Insufficient balance"));
    }

    @Test
    void testDebitSuccess() throws Exception {
        DebitCreditRequest debitRequest = new DebitCreditRequest(new BigDecimal("100.00"));
        AccountResponse response = new AccountResponse(1L, "John Doe", new BigDecimal("900.00"), "john_doe", LocalDateTime.now());

        when(accountService.debit(eq(1L), any(DebitCreditRequest.class))).thenReturn(response);

        mockMvc.perform(put("/accounts/1/debit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(debitRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(900.00));
    }
}
