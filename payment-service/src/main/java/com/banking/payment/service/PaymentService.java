package com.banking.payment.service;

import com.banking.payment.client.AccountClient;
import com.banking.payment.client.PaymentGatewayClient;
import com.banking.payment.dto.ChargeRequest;
import com.banking.payment.dto.ChargeResponse;
import com.banking.payment.dto.DebitCreditRequest;
import com.banking.payment.dto.PaymentRequest;
import com.banking.payment.dto.PaymentResponse;
import com.banking.payment.entity.Payment;
import com.banking.payment.entity.PaymentStatus;
import com.banking.payment.event.PaymentEvent;
import com.banking.payment.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AccountClient accountClient;
    private final PaymentGatewayClient paymentGatewayClient;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public static final String TOPIC = "payment-events";

    @Transactional(rollbackFor = Exception.class)
    @CircuitBreaker(name = "paymentGatewayCB", fallbackMethod = "processPaymentFallback")
    public PaymentResponse processPayment(PaymentRequest request) {
        // 1. Idempotency Check
        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existing.isPresent()) {
            return mapToResponse(existing.get());
        }

        String paymentId = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.PENDING)
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        paymentRepository.save(payment);

        // 2. Step 1 of SAGA: Debit Source Account via Feign
        try {
            accountClient.debit(request.getFromAccountId(), new DebitCreditRequest(request.getAmount()));
        } catch (Exception e) {
            log.error("Failed to debit account {}: {}", request.getFromAccountId(), e.getMessage());
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Debit failed: " + e.getMessage());
            paymentRepository.save(payment);
            publishEvent(payment);
            return mapToResponse(payment);
        }

        // 3. Step 2 of SAGA: Charge External Payment Gateway via Feign
        ChargeResponse chargeResponse;
        try {
            chargeResponse = paymentGatewayClient.charge(ChargeRequest.builder()
                    .idempotencyKey("PG-" + request.getIdempotencyKey())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .build());
        } catch (Exception e) {
            log.error("Payment Gateway call failed: {}", e.getMessage());
            // Compensation: Refund source account
            compensateDebit(request.getFromAccountId(), request.getAmount());
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Gateway call exception: " + e.getMessage());
            paymentRepository.save(payment);
            publishEvent(payment);
            return mapToResponse(payment);
        }

        if (!"SUCCESS".equalsIgnoreCase(chargeResponse.getStatus())) {
            log.warn("Payment Gateway declined transaction: {}", chargeResponse.getGatewayRef());
            // Compensation: Refund source account
            compensateDebit(request.getFromAccountId(), request.getAmount());
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Gateway declined payment");
            paymentRepository.save(payment);
            publishEvent(payment);
            return mapToResponse(payment);
        }

        // 4. Step 3 of SAGA: Credit Target Account via Feign
        try {
            accountClient.credit(request.getToAccountId(), new DebitCreditRequest(request.getAmount()));
        } catch (Exception e) {
            log.error("Failed to credit target account {}: {}", request.getToAccountId(), e.getMessage());
            // Compensation: Refund source account
            compensateDebit(request.getFromAccountId(), request.getAmount());
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Credit failed: " + e.getMessage());
            paymentRepository.save(payment);
            publishEvent(payment);
            return mapToResponse(payment);
        }

        // 5. Success
        payment.setStatus(PaymentStatus.SUCCESS);
        Payment saved = paymentRepository.save(payment);
        publishEvent(saved);
        return mapToResponse(saved);
    }

    public PaymentResponse processPaymentFallback(PaymentRequest request, Throwable t) {
        log.error("Circuit breaker fallback executed for payment: {}", t.getMessage());
        String paymentId = UUID.randomUUID().toString();
        Payment payment = Payment.builder()
                .paymentId(paymentId)
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.FAILED)
                .idempotencyKey(request.getIdempotencyKey())
                .failureReason("Circuit breaker open / Gateway unavailable: " + t.getMessage())
                .build();

        paymentRepository.save(payment);
        publishEvent(payment);
        return mapToResponse(payment);
    }

    private void compensateDebit(Long accountId, java.math.BigDecimal amount) {
        try {
            accountClient.credit(accountId, new DebitCreditRequest(amount));
            log.info("Compensation successful: Credited back {} to account {}", amount, accountId);
        } catch (Exception e) {
            log.error("CRITICAL: Failed to compensate account {}: {}", accountId, e.getMessage());
        }
    }

    private void publishEvent(Payment payment) {
        PaymentEvent event = PaymentEvent.builder()
                .paymentId(payment.getPaymentId())
                .fromAccountId(payment.getFromAccountId())
                .toAccountId(payment.getToAccountId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .timestamp(LocalDateTime.now())
                .build();
        try {
            kafkaTemplate.send(TOPIC, payment.getPaymentId(), event);
        } catch (Exception e) {
            log.error("Failed to send Kafka event for payment {}: {}", payment.getPaymentId(), e.getMessage());
        }
    }

    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoSuchElementException("Payment not found: " + paymentId));
        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .fromAccountId(payment.getFromAccountId())
                .toAccountId(payment.getToAccountId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .idempotencyKey(payment.getIdempotencyKey())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
