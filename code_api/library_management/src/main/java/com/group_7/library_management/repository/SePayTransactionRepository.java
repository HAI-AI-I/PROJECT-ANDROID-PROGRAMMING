package com.group_7.library_management.repository;

import com.group_7.library_management.entity.SePayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SePayTransactionRepository extends JpaRepository<SePayTransaction, Long> {
    boolean existsBySePayTransactionId(Long sePayTransactionId);
}
