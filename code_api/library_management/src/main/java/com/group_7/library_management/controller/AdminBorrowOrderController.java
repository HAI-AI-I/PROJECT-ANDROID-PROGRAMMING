package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BorrowOrderResponse;
import com.group_7.library_management.dto.PagedResponse;
import com.group_7.library_management.service.BorrowOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/borrow-orders")
public class AdminBorrowOrderController {
    private final BorrowOrderService borrowOrderService;

    public AdminBorrowOrderController(BorrowOrderService borrowOrderService) {
        this.borrowOrderService = borrowOrderService;
    }

    @GetMapping
    public PagedResponse<BorrowOrderResponse> getOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        int safePage = Math.max(page, 1) - 1;
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        return PagedResponse.from(borrowOrderService.getOrdersForAdmin(
                search,
                status,
                PageRequest.of(
                        safePage,
                        safePageSize,
                        Sort.by(Sort.Direction.DESC, "createdAt")
                )
        ));
    }

    @GetMapping("/books/{bookId}")
    public List<BorrowOrderResponse> getBookHistory(@PathVariable Long bookId) {
        return borrowOrderService.getBookHistory(bookId);
    }

    @GetMapping("/return-search")
    public BorrowOrderResponse findForReturn(@RequestParam String query) {
        return borrowOrderService.findOrderForReturn(query);
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

    @PostMapping("/{referenceCode}/refund-deposit")
    public BorrowOrderResponse confirmDepositRefund(@PathVariable String referenceCode) {
        return borrowOrderService.confirmDepositRefund(referenceCode);
    }
}
