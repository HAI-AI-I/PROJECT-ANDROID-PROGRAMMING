package com.group_7.library_management.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AuthorRequest(
        Long id,

        @Size(max = 150, message = "Tên tác giả không được dài quá 150 ký tự")
        String name,

        @Size(max = 150, message = "Bút danh không được dài quá 150 ký tự")
        String penName,

        LocalDate birthDate,
        LocalDate deathDate,

        @Size(max = 100, message = "Quốc tịch không được dài quá 100 ký tự")
        String nationality
) {
}
