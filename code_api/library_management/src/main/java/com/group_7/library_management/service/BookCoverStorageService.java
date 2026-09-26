package com.group_7.library_management.service;

import com.group_7.library_management.config.StorageProperties;
import com.group_7.library_management.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@Service
public class BookCoverStorageService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;

    private final Path bookCoverDirectory;

    public BookCoverStorageService(StorageProperties storageProperties) {
        this.bookCoverDirectory = storageProperties.getBookCoversPath();
    }

    public String store(MultipartFile file) {
        validateFile(file);

        String extension = detectExtension(file);
        String storedFileName = UUID.randomUUID().toString().toLowerCase(Locale.ROOT) + extension;
        Path destination = bookCoverDirectory.resolve(storedFileName).normalize();

        if (!destination.getParent().equals(bookCoverDirectory)) {
            throw new BadRequestException("Tên file ảnh bìa không hợp lệ");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.createDirectories(bookCoverDirectory);
            Files.copy(inputStream, destination);
            return storedFileName;
        } catch (IOException exception) {
            throw new IllegalStateException("Không thể lưu ảnh bìa sách", exception);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Vui lòng chọn ảnh bìa sách");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("Ảnh bìa không được vượt quá 5 MB");
        }
    }

    private String detectExtension(MultipartFile file) {
        byte[] header = readHeader(file);

        if (isJpeg(header)) {
            return ".jpg";
        }
        if (isPng(header)) {
            return ".png";
        }
        if (isWebP(header)) {
            return ".webp";
        }
        throw new BadRequestException("Ảnh bìa chỉ hỗ trợ định dạng JPEG, PNG hoặc WebP");
    }

    private byte[] readHeader(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return inputStream.readNBytes(12);
        } catch (IOException exception) {
            throw new BadRequestException("Không thể đọc file ảnh bìa");
        }
    }

    private boolean isJpeg(byte[] header) {
        return header.length >= 3
                && unsigned(header[0]) == 0xFF
                && unsigned(header[1]) == 0xD8
                && unsigned(header[2]) == 0xFF;
    }

    private boolean isPng(byte[] header) {
        int[] signature = {0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        if (header.length < signature.length) {
            return false;
        }
        for (int index = 0; index < signature.length; index++) {
            if (unsigned(header[index]) != signature[index]) {
                return false;
            }
        }
        return true;
    }

    private boolean isWebP(byte[] header) {
        return header.length >= 12
                && header[0] == 'R'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == 'F'
                && header[8] == 'W'
                && header[9] == 'E'
                && header[10] == 'B'
                && header[11] == 'P';
    }

    private int unsigned(byte value) {
        return Byte.toUnsignedInt(value);
    }
}
