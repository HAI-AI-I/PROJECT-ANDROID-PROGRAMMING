package com.group_7.library_management.dto;

import com.group_7.library_management.entity.Publisher;

public record PublisherResponse(
        Long id,
        String name,
        String address,
        String email,
        String phone
) {
    public static PublisherResponse from(Publisher publisher) {
        if (publisher == null) {
            return null;
        }
        return new PublisherResponse(
                publisher.getId(),
                publisher.getName(),
                publisher.getAddress(),
                publisher.getEmail(),
                publisher.getPhone()
        );
    }
}
