# Kế hoạch triển khai: Thêm tính năng quản lý Mượn - Trả sách

Thêm thực thể dữ liệu, DAO, Kho lưu trữ (Repository) và cấu hình lại Database để hỗ trợ tính năng theo dõi lịch sử mượn trả sách, phục vụ cho các màn hình `BORROW` và `HISTORY`.

## Thách thức & Thiết kế Cấu trúc
Ứng dụng hiện tại đang sử dụng Room Database ở phiên bản `3`. Việc thêm bảng mới yêu cầu nâng phiên bản database (`version = 4`) và cấu hình cơ chế tự động xóa dữ liệu cũ khi nâng cấp (`fallbackToDestructiveMigration`) để tránh crash trong quá trình phát triển (vốn đã được cài đặt sẵn trong `DatabaseModule`).

## Các thay đổi đề xuất

### 1. Tầng Dữ liệu Cục bộ (Local Data)

#### [NEW] [BorrowReceiptEntity.kt](file:///D:/ProjectLTTBDD/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/data/local/entity/BorrowReceiptEntity.kt)
Tạo thực thể lưu trữ thông tin chi tiết một lần mượn sách.
- `receiptId: Long` (Tự động tăng)
- `userId: Long`
- `bookId: String`
- `borrowDate: String` (Định dạng dd/MM/yyyy để đồng bộ dữ liệu với UserEntity)
- `dueDate: String`
- `returnDate: String?` (null nếu chưa trả)
- `status: String` ("BORROWED" - Đang mượn, "RETURNED" - Đã trả, "OVERDUE" - Quá hạn)

#### [NEW] [BorrowReceiptDAO.kt](file:///D:/ProjectLTTBDD/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/data/local/dao/BorrowReceiptDAO.kt)
Tạo giao diện truy vấn dữ liệu mượn trả:
- Thêm phiếu mượn mới.
- Cập nhật trạng thái trả sách (điền ngày trả thực tế và đổi trạng thái sang "RETURNED").
- Lấy danh sách sách đang mượn của một User (Phục vụ màn hình `Routes.BORROW`).
- Lấy toàn bộ lịch sử mượn trả của một User (Phục vụ màn hình `Routes.HISTORY`).
- Đếm số sách đang mượn và tổng số sách đã đọc (Dùng để hiển thị động lên Profile).

#### [MODIFY] [BookDAO.kt](file:///D:/ProjectLTTBDD/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/data/local/dao/BookDAO.kt)
Bổ sung các hàm cập nhật kho sách:
- Cập nhật giảm số lượng bản sách còn lại `availableCopies = availableCopies - 1` khi mượn.
- Cập nhật tăng lại số lượng bản sách `availableCopies = availableCopies + 1` khi trả.

#### [MODIFY] [AppDatabase.kt](file:///D:/ProjectLTTBDD/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/data/local/AppDatabase.kt)
- Thêm `BorrowReceiptEntity::class` vào danh sách `entities`.
- Tăng `version` từ `3` lên `4`.
- Khai báo hàm trừu tượng `abstract fun getBorrowReceiptDao(): BorrowReceiptDAO`.

---

### 2. Tầng Điều phối (Repository)

#### [NEW] [BorrowRepository.kt](file:///D:/ProjectLTTBDD/PROJECT-ANDROID-PROGRAMMING/Code/app/src/main/java/com/group_7/library_management/data/repository/BorrowRepository.kt)
Quản lý luồng xử lý đồng thời (Transaction) giữa việc tạo phiếu mượn và giảm số lượng sách trong kho:
- Hàm mượn sách `borrowBook(userId, bookId, durationDays): Result<Boolean>`.
- Hàm trả sách `returnBook(receiptId, bookId): Result<Boolean>`.
- Các hàm lấy danh sách đang mượn, lịch sử mượn, và các hàm đếm số lượng thống kê.

---

### 3. Tầng Cung cấp Phụ thuộc (Dependency Injection)

#### [MODIFY] [DatabaseModule.kt](file:///D:/ProjectLTTBDD/PROJECT-ANDROID-PROGRAMMING/Code/di/DatabaseModule.kt)
- Cung cấp `BorrowReceiptDAO` qua `AppDatabase`.
- Khởi tạo và cung cấp Singleton `BorrowRepository`.

## Kế hoạch kiểm tra
- Kiểm tra biên dịch ứng dụng không phát sinh lỗi Gradle.
- Đảm bảo Room Database tự động tạo bảng `borrow_receipts` khi khởi chạy phiên bản 4.
