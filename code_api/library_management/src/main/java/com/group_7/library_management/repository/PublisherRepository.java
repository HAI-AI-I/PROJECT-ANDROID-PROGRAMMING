package com.group_7.library_management.repository;

import com.group_7.library_management.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    Optional<Publisher> findByNameIgnoreCase(String name);

    List<Publisher> findTop20ByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
