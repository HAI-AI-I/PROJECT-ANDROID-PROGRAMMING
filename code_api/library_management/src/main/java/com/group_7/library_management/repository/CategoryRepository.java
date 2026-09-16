package com.group_7.library_management.repository;

import com.group_7.library_management.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlugIgnoreCase(String slug);

    Optional<Category> findByNameIgnoreCase(String name);

    List<Category> findAllByActiveTrueOrderByNameAsc();

    List<Category> findAllByOrderByNameAsc();
}
