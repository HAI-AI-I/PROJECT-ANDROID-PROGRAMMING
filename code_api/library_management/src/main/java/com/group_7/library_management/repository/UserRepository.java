package com.group_7.library_management.repository;

import com.group_7.library_management.entity.User;
import com.group_7.library_management.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    long countByRole(UserRole role);

    List<User> findAllByRoleAndActiveTrue(UserRole role);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findByIdForUpdate(@Param("userId") Long userId);

    public boolean existsByEmailIgnoreCase(String email);

    public boolean existsByPhone(String phone);

    public Optional<User> findByEmailIgnoreCase(String email);

    public Optional<User> findByPhone(String phone);

    public Optional<User> findByEmailIgnoreCaseOrPhone(String email, String phone);

    @Query("""
            select u from User u
            where u.role = com.group_7.library_management.entity.UserRole.USER
              and (:search is null
                   or lower(u.fullName) like lower(concat('%', :search, '%'))
                   or lower(u.email) like lower(concat('%', :search, '%'))
                   or u.phone like concat('%', :search, '%'))
              and (:active is null or u.active = :active)
            """)
    Page<User> searchReaders(
            @Param("search") String search,
            @Param("active") Boolean active,
            Pageable pageable
    );

    @Query("""
            select u from User u
            where (:search is null
                   or lower(u.fullName) like lower(concat('%', :search, '%'))
                   or lower(u.email) like lower(concat('%', :search, '%'))
                   or u.phone like concat('%', :search, '%'))
              and (:role is null or u.role = :role)
              and (:active is null or u.active = :active)
            """)
    Page<User> searchUsers(
            @Param("search") String search,
            @Param("role") UserRole role,
            @Param("active") Boolean active,
            Pageable pageable
    );
}
