package com.group_7.library_management.repository;

import com.group_7.library_management.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findAllByNameIgnoreCase(String name);

    List<Author> findTop20ByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
