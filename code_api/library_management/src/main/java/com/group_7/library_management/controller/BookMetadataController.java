package com.group_7.library_management.controller;

import com.group_7.library_management.dto.AuthorResponse;
import com.group_7.library_management.dto.CategoryResponse;
import com.group_7.library_management.dto.PublisherResponse;
import com.group_7.library_management.repository.AuthorRepository;
import com.group_7.library_management.repository.CategoryRepository;
import com.group_7.library_management.repository.PublisherRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookMetadataController {

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    public BookMetadataController(
            AuthorRepository authorRepository,
            CategoryRepository categoryRepository,
            PublisherRepository publisherRepository
    ) {
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
    }

    @GetMapping("/api/v1/authors")
    public List<AuthorResponse> searchAuthors(
            @RequestParam(defaultValue = "") String search
    ) {
        return authorRepository
                .findTop20ByNameContainingIgnoreCaseOrderByNameAsc(search.strip())
                .stream()
                .map(AuthorResponse::from)
                .toList();
    }

    @GetMapping("/api/v1/publishers")
    public List<PublisherResponse> searchPublishers(
            @RequestParam(defaultValue = "") String search
    ) {
        return publisherRepository
                .findTop20ByNameContainingIgnoreCaseOrderByNameAsc(search.strip())
                .stream()
                .map(PublisherResponse::from)
                .toList();
    }

    @GetMapping("/api/v1/categories")
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
