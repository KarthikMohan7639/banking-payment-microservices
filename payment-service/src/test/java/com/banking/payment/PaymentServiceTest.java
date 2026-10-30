package com.banking.payment;

import com.banking.payment.client.AccountClient;
import com.banking.payment.client.PaymentGatewayClient;
import com.banking.payment.dto.AccountResponse;
import com.banking.payment.dto.ChargeRequest;
import com.banking.payment.dto.ChargeResponse;
import com.banking.payment.dto.DebitCreditRequest;
import com.banking.payment.dto.PaymentRequest;
import com.banking.payment.dto.PaymentResponse;
import com.banking.payment.entity.Payment;
import com.banking.payment.entity.PaymentStatus;
import com.banking.payment.event.PaymentEvent;
import com.banking.payment.repository.PaymentRepository;
import com.banking.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountClient accountClient;

    @Mock
    private PaymentGatewayClient paymentGatewayClient;

    @Mock
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        paymentRequest = PaymentRequest.builder()
                .fromAccountId(1L)
                .toAccountId(2L)
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .idempotencyKey("KEY-PAYMENT-1")
                .build();
    }

    @Test
    void testProcessPaymentSuccess() {
        when(paymentRepository.findByIdempotencyKey("KEY-PAYMENT-1")).thenReturn(Optional.empty());
        when(accountClient.debit(eq(1L), any(DebitCreditRequest.class)))
                .thenReturn(new AccountResponse(1L, "John", new BigDecimal("900"), "john"));
        when(paymentGatewayClient.charge(any(ChargeRequest.class)))
                .thenReturn(new ChargeResponse("GW-123", "SUCCESS"));
        when(accountClient.credit(eq(2L), any(DebitCreditRequest.class)))
                .thenReturn(new AccountResponse(2L, "Alice", new BigDecimal("1100"), "alice"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response = paymentService.processPayment(paymentRequest);

        assertNotNull(response.getPaymentId());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        verify(accountClient).debit(eq(1L), any(DebitCreditRequest.class));
        verify(paymentGatewayClient).charge(any(ChargeRequest.class));
        verify(accountClient).credit(eq(2L), any(DebitCreditRequest.class));
    }

    @Test
    void testProcessPaymentDebitFailureNoCompensationNeeded() {
        when(paymentRepository.findByIdempotencyKey("KEY-PAYMENT-1")).thenReturn(Optional.empty());
        when(accountClient.debit(eq(1L), any(DebitCreditRequest.class)))
                .thenThrow(new RuntimeException("Insufficient Balance"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response = paymentService.processPayment(paymentRequest);

        assertEquals(PaymentStatus.FAILED, response.getStatus());
        verify(paymentGatewayClient, never()).charge(any());
        verify(accountClient, never()).credit(eq(2L), any());
    }

    @Test
    void testProcessPaymentGatewayFailureExecutesCompensation() {
        when(paymentRepository.findByIdempotencyKey("KEY-PAYMENT-1")).thenReturn(Optional.empty());
        when(accountClient.debit(eq(1L), any(DebitCreditRequest.class)))
                .thenReturn(new AccountResponse(1L, "John", new BigDecimal("900"), "john"));
        when(paymentGatewayClient.charge(any(ChargeRequest.class)))
                .thenReturn(new ChargeResponse("GW-ERR", "FAILED"));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse response = paymentService.processPayment(paymentRequest);

        assertEquals(PaymentStatus.FAILED, response.getStatus());
        // Verify compensation refund back to source account 1L
        verify(accountClient).credit(eq(1L), any(DebitCreditRequest.class));
    }
}
