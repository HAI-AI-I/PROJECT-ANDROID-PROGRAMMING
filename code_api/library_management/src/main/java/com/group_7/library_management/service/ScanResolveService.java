package com.group_7.library_management.service;

import com.group_7.library_management.dto.ScanResolveResponse;
import com.group_7.library_management.dto.ScanTargetType;
import com.group_7.library_management.exception.BadRequestException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.BookCopyRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.BorrowRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScanResolveService {
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    public ScanResolveService(
            BookRepository bookRepository,
            BookCopyRepository bookCopyRepository,
            BorrowRecordRepository borrowRecordRepository
    ) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.borrowRecordRepository = borrowRecordRepository;
    }

    @Transactional(readOnly = true)
    public ScanResolveResponse resolve(Long userId, String rawCode) {
        String code = rawCode == null ? "" : rawCode.trim();
        if (code.isEmpty()) {
            throw new BadRequestException("Mã quét không được để trống");
        }

        var borrowOrder = borrowRecordRepository.findByReferenceCodeAndUserId(code, userId);
        if (borrowOrder.isPresent()) {
            return new ScanResolveResponse(
                    ScanTargetType.BORROW_ORDER,
                    borrowOrder.get().getId()
            );
        }

        var bookCopy = bookCopyRepository.findByBarcodeAndBookActiveTrue(code);
        if (bookCopy.isPresent()) {
            return new ScanResolveResponse(
                    ScanTargetType.BOOK,
                    bookCopy.get().getBook().getId()
            );
        }

        var book = bookRepository.findByIsbnAndActiveTrue(code);
        if (book.isPresent()) {
            return new ScanResolveResponse(ScanTargetType.BOOK, book.get().getId());
        }

        throw new ResourceNotFoundException("Không tìm thấy sách hoặc đơn mượn phù hợp với mã đã quét");
    }
}
