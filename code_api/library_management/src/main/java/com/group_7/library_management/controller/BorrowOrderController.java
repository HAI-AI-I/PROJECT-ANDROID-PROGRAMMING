package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BorrowOrderResponse;
import com.group_7.library_management.dto.CreateBorrowOrderRequest;
import com.group_7.library_management.dto.CurrentBorrowOrderResponse;
import com.group_7.library_management.service.BorrowOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/borrow-orders")
public class BorrowOrderController {
    private final BorrowOrderService borrowOrderService;

    public BorrowOrderController(BorrowOrderService borrowOrderService) {
        this.borrowOrderService = borrowOrderService;
    }

    @GetMapping
    public List<BorrowOrderResponse> getOrders(Authentication authentication) {
        return borrowOrderService.getOrders((Long) authentication.getPrincipal());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BorrowOrderResponse createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateBorrowOrderRequest request
    ) {
        return borrowOrderService.createOrder(
                (Long) authentication.getPrincipal(),
                request
        );
    }

    @GetMapping("/{orderId}")
    public BorrowOrderResponse getOrder(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        return borrowOrderService.getOrder(
                (Long) authentication.getPrincipal(),
                orderId
        );
    }

    @GetMapping("/books/{bookId}/current")
    public CurrentBorrowOrderResponse getCurrentOrderForBook(
            Authentication authentication,
            @PathVariable Long bookId
    ) {
        return borrowOrderService.getCurrentOrderForBook(
                (Long) authentication.getPrincipal(),
                bookId
        );
    }
}
