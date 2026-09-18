package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BorrowOrderResponse;
import com.group_7.library_management.service.BorrowOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/borrow-orders")
public class AdminBorrowOrderController {
    private final BorrowOrderService borrowOrderService;

    public AdminBorrowOrderController(BorrowOrderService borrowOrderService) {
        this.borrowOrderService = borrowOrderService;
    }

    @GetMapping("/{referenceCode}")
    public BorrowOrderResponse getByReferenceCode(@PathVariable String referenceCode) {
        return borrowOrderService.getOrderForLibrarian(referenceCode);
    }

    @PostMapping("/{referenceCode}/pickup")
    public BorrowOrderResponse confirmPickup(@PathVariable String referenceCode) {
        return borrowOrderService.confirmPickup(referenceCode);
    }

    @PostMapping("/{referenceCode}/return")
    public BorrowOrderResponse confirmReturn(@PathVariable String referenceCode) {
        return borrowOrderService.confirmReturn(referenceCode);
    }
}
