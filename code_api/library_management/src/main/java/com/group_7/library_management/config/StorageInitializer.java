package com.group_7.library_management.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class StorageInitializer implements ApplicationRunner {

    private final StorageProperties storageProperties;

    public StorageInitializer(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        Path bookCoverDirectory = storageProperties.getBookCoversPath();
        try {
            Files.createDirectories(bookCoverDirectory);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Không thể khởi tạo thư mục lưu ảnh bìa: " + bookCoverDirectory,
                    exception
            );
        }
    }
}
