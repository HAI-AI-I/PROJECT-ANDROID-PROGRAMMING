package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BorrowPaymentResponse;
import com.group_7.library_management.dto.SePayWebhookResponse;
import com.group_7.library_management.service.SePayPaymentService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final SePayPaymentService paymentService;

    public PaymentController(SePayPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/borrow-orders/{orderId}")
    public BorrowPaymentResponse getBorrowPayment(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        return paymentService.getPayment((Long) authentication.getPrincipal(), orderId);
    }

    @PostMapping("/sepay/webhook")
    public SePayWebhookResponse receiveSePayWebhook(
            @RequestBody byte[] rawBody,
            @RequestHeader("X-SePay-Signature") String signature,
            @RequestHeader("X-SePay-Timestamp") String timestamp
    ) {
        paymentService.processWebhook(rawBody, signature, timestamp);
        return new SePayWebhookResponse(true);
    }
}
