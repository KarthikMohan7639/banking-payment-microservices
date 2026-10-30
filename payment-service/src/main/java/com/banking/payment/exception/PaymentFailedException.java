package com.banking.payment.exception;

/**
 * Custom runtime exception thrown when SAGA payment transfer fails.
 */
public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}
