package com.group_7.library_management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@ConfigurationProperties(prefix = "app.storage")
public class StorageProperties {

    private String bookCovers = "uploads/book-covers";

    public String getBookCovers() {
        return bookCovers;
    }

    public void setBookCovers(String bookCovers) {
        this.bookCovers = bookCovers;
    }

    public Path getBookCoversPath() {
        return Path.of(bookCovers).toAbsolutePath().normalize();
    }
}
