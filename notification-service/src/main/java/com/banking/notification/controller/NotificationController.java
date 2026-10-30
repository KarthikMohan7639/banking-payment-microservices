package com.banking.notification.controller;

import com.banking.notification.entity.NotificationLog;
import com.banking.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationLogRepository repository;

    @GetMapping
    public ResponseEntity<List<NotificationLog>> getAllNotifications() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<NotificationLog>> getNotificationsByPaymentId(@PathVariable String paymentId) {
        return ResponseEntity.ok(repository.findByPaymentId(paymentId));
    }
}
