package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BorrowOrderResponse;
import com.group_7.library_management.dto.CreateBorrowOrderRequest;
import com.group_7.library_management.dto.CurrentBorrowOrderResponse;
import com.group_7.library_management.dto.CancelBorrowOrderResponse;
import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.service.BorrowOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/borrow-orders")
public class BorrowOrderController {
    private final BorrowOrderService borrowOrderService;

    public BorrowOrderController(BorrowOrderService borrowOrderService) {
        this.borrowOrderService = borrowOrderService;
    }

    @GetMapping
    public PagedResponse<BorrowOrderResponse> getOrders(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        int safePage = Math.max(page, 1) - 1;
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        return PagedResponse.from(borrowOrderService.getOrders(
                (Long) authentication.getPrincipal(),
                status,
                PageRequest.of(
                        safePage,
                        safePageSize,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        ));
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

    @PostMapping("/{orderId}/cancel")
    public CancelBorrowOrderResponse cancelOrder(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        return borrowOrderService.cancelOrder(
                (Long) authentication.getPrincipal(),
                orderId
        );
    }
}
