package com.group_7.library_management.controller;

import com.group_7.library_management.dto.BookCoverUploadResponse;
import com.group_7.library_management.service.BookCoverStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/admin/book-covers")
public class AdminBookCoverController {

    private static final String PUBLIC_PATH = "/uploads/book-covers/";

    private final BookCoverStorageService bookCoverStorageService;

    public AdminBookCoverController(BookCoverStorageService bookCoverStorageService) {
        this.bookCoverStorageService = bookCoverStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public BookCoverUploadResponse upload(@RequestParam("file") MultipartFile file) {
        String fileName = bookCoverStorageService.store(file);
        return toResponse(fileName);
    }

    private BookCoverUploadResponse toResponse(String fileName) {
        String path = PUBLIC_PATH + fileName;
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(path)
                .toUriString();

        return new BookCoverUploadResponse(fileName, path, url);
    }
}
