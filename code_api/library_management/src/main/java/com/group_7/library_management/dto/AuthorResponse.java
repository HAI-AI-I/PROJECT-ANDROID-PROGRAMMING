package com.group_7.library_management.dto;

import com.group_7.library_management.entity.Author;

import java.time.LocalDate;

public record AuthorResponse(
        Long id,
        String name,
        String penName,
        LocalDate birthDate,
        LocalDate deathDate,
        String nationality
) {
    public static AuthorResponse from(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getPenName(),
                author.getBirthDate(),
                author.getDeathDate(),
                author.getNationality()
        );
    }
}
