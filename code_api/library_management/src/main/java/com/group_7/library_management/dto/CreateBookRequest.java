package com.group_7.library_management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateBookRequest(
        @Size(max = 20, message = "ISBN không được dài quá 20 ký tự")
        String isbn,

        @NotBlank(message = "Tên sách không được để trống")
        @Size(max = 250, message = "Tên sách không được dài quá 250 ký tự")
        String title,

        @Size(max = 150, message = "Tên tác giả không được dài quá 150 ký tự")
        String author,

        @Valid
        List<AuthorRequest> authors,

        @NotBlank(message = "Thể loại không được để trống")
        @Size(max = 100, message = "Tên thể loại không được dài quá 100 ký tự")
        String category,

        @Size(max = 150, message = "Tên nhà xuất bản không được dài quá 150 ký tự")
        String publisher,

        @Valid
        PublisherRequest publisherDetails,

        @Min(value = 1000, message = "Năm xuất bản không hợp lệ")
        @Max(value = 2100, message = "Năm xuất bản không hợp lệ")
        Integer publishYear,

        @NotNull(message = "Số lượng sách không được để trống")
        @Min(value = 1, message = "Số lượng sách phải lớn hơn 0")
        @Max(value = 10000, message = "Số lượng sách quá lớn")
        Integer quantity,

        @Size(max = 1000, message = "Đường dẫn ảnh bìa quá dài")
        String cover,

        @Size(max = 5000, message = "Mô tả không được dài quá 5000 ký tự")
        String description,

        @PositiveOrZero(message = "Phí mượn không được âm")
        Long borrowFee,

        @Size(max = 100, message = "Vị trí kệ sách quá dài")
        String shelfLocation
) {
}
