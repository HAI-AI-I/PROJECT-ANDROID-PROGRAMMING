package com.group_7.library_management.service;

import com.group_7.library_management.dto.BookCopyResponse;
import com.group_7.library_management.dto.BookDetailResponse;
import com.group_7.library_management.dto.BookResponse;
import com.group_7.library_management.dto.AuthorRequest;
import com.group_7.library_management.dto.CreateBookRequest;
import com.group_7.library_management.dto.UpdateBookRequest;
import com.group_7.library_management.dto.PublisherRequest;
import com.group_7.library_management.dto.PopularBookResponse;
import com.group_7.library_management.entity.Author;
import com.group_7.library_management.entity.Book;
import com.group_7.library_management.entity.BookCopy;
import com.group_7.library_management.entity.BookCopyStatus;
import com.group_7.library_management.entity.Category;
import com.group_7.library_management.entity.Publisher;
import com.group_7.library_management.exception.BadRequestException;
import com.group_7.library_management.exception.ConflictException;
import com.group_7.library_management.exception.ResourceNotFoundException;
import com.group_7.library_management.repository.AuthorRepository;
import com.group_7.library_management.repository.BookCopyRepository;
import com.group_7.library_management.repository.BookRepository;
import com.group_7.library_management.repository.CategoryRepository;
import com.group_7.library_management.repository.PublisherRepository;
import com.group_7.library_management.repository.BookPopularityStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final BookAvailabilitySubscriptionService availabilitySubscriptionService;

    public BookService(
        BookRepository bookRepository,
            BookCopyRepository bookCopyRepository,
            AuthorRepository authorRepository,
            CategoryRepository categoryRepository,
            PublisherRepository publisherRepository,
            BookAvailabilitySubscriptionService availabilitySubscriptionService
    ) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
        this.availabilitySubscriptionService = availabilitySubscriptionService;
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        String isbn = normalizeNullable(request.isbn());
        ensureIsbnAvailable(isbn, null);

        Category category = resolveCategory(request.category());
        Book book = new Book(normalizeRequired(request.title(), "Tên sách"), category);
        book.setIsbn(isbn);
        book.setDescription(normalizeNullable(request.description()));
        book.setPublishYear(request.publishYear());
        book.setCoverImageUrl(normalizeNullable(request.cover()));
        book.setBorrowFee(request.borrowFee() == null ? 0L : request.borrowFee());
        book.setAuthors(resolveAuthors(request.author(), request.authors()));
        book.setPublisher(resolvePublisher(request.publisher(), request.publisherDetails()));

        Book savedBook = bookRepository.save(book);
        addCopies(
                savedBook,
                request.quantity(),
                normalizeNullable(request.shelfLocation())
        );
        bookCopyRepository.flush();
        return toResponse(savedBook);
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooks(
            String keyword,
            String category,
            String status,
            Pageable pageable
    ) {
        String normalizedKeyword = normalizeNullable(keyword);
        String categorySlug = normalizeNullable(category);
        if (categorySlug != null) {
            categorySlug = slugify(categorySlug);
        }
        String availabilityStatus = normalizeAvailabilityStatus(status);
        return bookRepository.searchActiveBooks(
                        normalizedKeyword,
                        categorySlug,
                        availabilityStatus,
                        pageable
                )
                .map(this::toResponse);
    }

    private String normalizeAvailabilityStatus(String status) {
        String normalizedStatus = normalizeNullable(status);
        if (normalizedStatus == null) {
            return null;
        }
        normalizedStatus = normalizedStatus.toLowerCase(Locale.ROOT);
        if (!Set.of("available", "borrowed", "out_of_stock").contains(normalizedStatus)) {
            throw new BadRequestException("Trạng thái sách không hợp lệ");
        }
        return normalizedStatus;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getLatestBooks(int limit) {
        Pageable newestBooks = PageRequest.of(
                0,
                limit,
                Sort.by(Sort.Direction.DESC, "createdAt")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );
        return bookRepository.findAllByActiveTrue(newestBooks)
                .getContent()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PopularBookResponse> getPopularBooks(int limit) {
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        List<BookPopularityStatistics> statistics = bookRepository
                .findPopularBooksSince(thirtyDaysAgo, PageRequest.of(0, limit));

        Map<Long, Book> booksById = bookRepository
                .findAllByIdInAndActiveTrue(
                        statistics.stream().map(BookPopularityStatistics::getBookId).toList()
                )
                .stream()
                .collect(Collectors.toMap(Book::getId, book -> book));

        return statistics.stream()
                .filter(item -> booksById.containsKey(item.getBookId()))
                .map(item -> new PopularBookResponse(
                        toResponse(booksById.get(item.getBookId())),
                        item.getBorrowCount(),
                        item.getFavoriteCount(),
                        item.getNotificationClickCount(),
                        item.getPopularityScore()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PopularBookResponse> getRelatedBooks(Long bookId, int limit) {
        Book currentBook = bookRepository.findByIdAndActiveTrue(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        List<BookPopularityStatistics> statistics = bookRepository.findRelatedPopularBooksSince(
                currentBook.getCategory().getId(),
                bookId,
                thirtyDaysAgo,
                PageRequest.of(0, limit)
        );

        Map<Long, Book> booksById = bookRepository
                .findAllByIdInAndActiveTrue(
                        statistics.stream().map(BookPopularityStatistics::getBookId).toList()
                )
                .stream()
                .collect(Collectors.toMap(Book::getId, book -> book));

        return statistics.stream()
                .filter(item -> booksById.containsKey(item.getBookId()))
                .map(item -> new PopularBookResponse(
                        toResponse(booksById.get(item.getBookId())),
                        item.getBorrowCount(),
                        item.getFavoriteCount(),
                        item.getNotificationClickCount(),
                        item.getPopularityScore()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public BookDetailResponse getBookDetail(Long id) {
        Book book = bookRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
        List<BookCopyResponse> copies = bookCopyRepository.findAllByBookIdOrderByIdAsc(id)
                .stream()
                .map(BookCopyResponse::from)
                .toList();
        return new BookDetailResponse(toResponse(book), copies);
    }

    @Transactional(readOnly = true)
    public BookResponse getBook(Long id) {
        Book book = bookRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
        return toResponse(book);
    }

    @Transactional
    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));

        if (request.isbn() != null) {
            String isbn = normalizeNullable(request.isbn());
            ensureIsbnAvailable(isbn, id);
            book.setIsbn(isbn);
        }
        if (request.title() != null) {
            book.setTitle(normalizeRequired(request.title(), "Tên sách"));
        }
        if (request.authors() != null) {
            book.setAuthors(resolveAuthors(null, request.authors()));
        } else if (request.author() != null) {
            book.setAuthors(resolveAuthors(request.author(), null));
        }
        if (request.category() != null) {
            book.setCategory(resolveCategory(request.category()));
        }
        if (request.publisherDetails() != null) {
            book.setPublisher(resolvePublisher(null, request.publisherDetails()));
        } else if (request.publisher() != null) {
            book.setPublisher(resolvePublisher(request.publisher(), null));
        }
        if (request.publishYear() != null) {
            book.setPublishYear(request.publishYear());
        }
        if (request.cover() != null) {
            book.setCoverImageUrl(normalizeNullable(request.cover()));
        }
        if (request.description() != null) {
            book.setDescription(normalizeNullable(request.description()));
        }
        if (request.borrowFee() != null) {
            book.setBorrowFee(request.borrowFee());
        }
        if (request.active() != null) {
            book.setActive(request.active());
        }

        boolean shelfLocationProvided = request.shelfLocation() != null;
        String shelfLocation = request.shelfLocation() == null
                ? null
                : normalizeNullable(request.shelfLocation());
        if (request.quantity() != null) {
            adjustQuantity(book, request.quantity(), shelfLocation, shelfLocationProvided);
        } else if (shelfLocationProvided) {
            updateShelfLocation(book.getId(), shelfLocation);
        }

        bookRepository.save(book);
        bookCopyRepository.flush();
        return toResponse(book);
    }

    @Transactional
    public void deactivateBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách"));
        book.setActive(false);
    }

    private void adjustQuantity(
            Book book,
            int requestedQuantity,
            String shelfLocation,
            boolean updateShelfLocation
    ) {
        List<BookCopy> existingCopies = bookCopyRepository.findAllByBookIdOrderByIdAsc(book.getId());
        int currentQuantity = existingCopies.size();
        long previousAvailable = existingCopies.stream()
                .filter(copy -> copy.getStatus() == BookCopyStatus.AVAILABLE)
                .count();
        if (updateShelfLocation) {
            existingCopies.forEach(copy -> copy.setShelfLocation(shelfLocation));
        }
        if (requestedQuantity > currentQuantity) {
            String shelfForNewCopies = updateShelfLocation
                    ? shelfLocation
                    : existingCopies.stream()
                            .map(BookCopy::getShelfLocation)
                            .filter(value -> value != null && !value.isBlank())
                            .findFirst()
                            .orElse(null);
            addCopies(
                    book,
                    requestedQuantity - currentQuantity,
                    shelfForNewCopies
            );
            availabilitySubscriptionService.notifyAvailabilityChanged(
                    book,
                    previousAvailable,
                    previousAvailable + requestedQuantity - currentQuantity
            );
            return;
        }
        if (requestedQuantity < currentQuantity) {
            int numberToRemove = currentQuantity - requestedQuantity;
            List<BookCopy> removableCopies = existingCopies.stream()
                    .filter(copy -> copy.getStatus() == BookCopyStatus.AVAILABLE)
                    .sorted(Comparator.comparing(BookCopy::getId).reversed())
                    .limit(numberToRemove)
                    .toList();
            if (removableCopies.size() < numberToRemove) {
                throw new BadRequestException(
                        "Không thể giảm số lượng vì có bản sách đang được mượn, đặt trước hoặc không khả dụng"
                );
            }
            bookCopyRepository.deleteAll(removableCopies);
            availabilitySubscriptionService.notifyAvailabilityChanged(
                    book,
                    previousAvailable,
                    previousAvailable - removableCopies.size()
            );
        }
    }

    private void updateShelfLocation(Long bookId, String shelfLocation) {
        bookCopyRepository.findAllByBookIdOrderByIdAsc(bookId)
                .forEach(copy -> copy.setShelfLocation(shelfLocation));
    }

    private void addCopies(Book book, int numberOfCopies, String shelfLocation) {
        List<BookCopy> copies = new ArrayList<>(numberOfCopies);
        for (int index = 0; index < numberOfCopies; index++) {
            BookCopy copy = new BookCopy(createBarcode(book.getId()), book);
            copy.setShelfLocation(shelfLocation);
            copy.setAcquiredDate(LocalDate.now());
            copies.add(copy);
        }
        bookCopyRepository.saveAll(copies);
    }

    private String createBarcode(Long bookId) {
        return "BOOK-%06d-%s".formatted(
                bookId,
                UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT)
        );
    }

    private Set<Author> resolveAuthors(String rawAuthors, List<AuthorRequest> authorDetails) {
        if (authorDetails != null) {
            if (authorDetails.isEmpty()) {
                throw new BadRequestException("Sách phải có ít nhất một tác giả");
            }
            return authorDetails.stream()
                    .map(this::resolveAuthor)
                    .collect(Collectors.toSet());
        }

        Map<String, String> names = new LinkedHashMap<>();
        for (String item : normalizeRequired(rawAuthors, "Tác giả").split(",")) {
            String name = normalizeRequired(item, "Tác giả");
            names.putIfAbsent(name.toLowerCase(Locale.ROOT), name);
        }
        return names.values().stream()
                .map(this::resolveLegacyAuthor)
                .collect(Collectors.toSet());
    }

    private Author resolveLegacyAuthor(String name) {
        List<Author> matches = authorRepository.findAllByNameIgnoreCase(name);
        if (matches.size() == 1) {
            return matches.getFirst();
        }
        if (matches.size() > 1) {
            throw new ConflictException(
                    "Có nhiều tác giả cùng tên '" + name
                            + "'. Hãy gửi authors với id hoặc thông tin chi tiết để chọn đúng người"
            );
        }
        return authorRepository.save(new Author(name));
    }

    private Author resolveAuthor(AuthorRequest request) {
        if (request == null) {
            throw new BadRequestException("Thông tin tác giả không hợp lệ");
        }
        if (request.id() != null) {
            return authorRepository.findById(request.id())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy tác giả có id " + request.id()
                    ));
        }

        String name = normalizeRequired(request.name(), "Tên tác giả");
        validateAuthorDates(request);
        String penName = normalizeNullable(request.penName());
        String nationality = normalizeNullable(request.nationality());

        List<Author> sameNameAuthors = authorRepository.findAllByNameIgnoreCase(name);
        if (!hasIdentityDetails(request)) {
            if (sameNameAuthors.size() == 1) {
                return sameNameAuthors.getFirst();
            }
            if (sameNameAuthors.size() > 1) {
                throw ambiguousAuthor(name);
            }
            return createAuthor(name, request, penName, nationality);
        }

        List<Author> exactAuthors = sameNameAuthors.stream()
                .filter(author -> request.birthDate() == null
                        || request.birthDate().equals(author.getBirthDate()))
                .filter(author -> exactText(penName, author.getPenName()))
                .filter(author -> exactText(nationality, author.getNationality()))
                .toList();
        if (exactAuthors.size() == 1) {
            Author existing = exactAuthors.getFirst();
            enrichAuthor(existing, request, penName, nationality);
            return existing;
        }
        if (exactAuthors.size() > 1) {
            throw ambiguousAuthor(name);
        }

        List<Author> compatibleAuthors = sameNameAuthors.stream()
                .filter(author -> request.birthDate() == null
                        || author.getBirthDate() == null
                        || request.birthDate().equals(author.getBirthDate()))
                .filter(author -> compatibleText(penName, author.getPenName()))
                .filter(author -> compatibleText(nationality, author.getNationality()))
                .toList();

        if (compatibleAuthors.size() == 1) {
            Author existing = compatibleAuthors.getFirst();
            enrichAuthor(existing, request, penName, nationality);
            return existing;
        }
        if (compatibleAuthors.size() > 1) {
            throw ambiguousAuthor(name);
        }
        return createAuthor(name, request, penName, nationality);
    }

    private Author createAuthor(
            String name,
            AuthorRequest request,
            String penName,
            String nationality
    ) {
        Author author = new Author(name);
        author.setPenName(penName);
        author.setBirthDate(request.birthDate());
        author.setDeathDate(request.deathDate());
        author.setNationality(nationality);
        return authorRepository.save(author);
    }

    private ConflictException ambiguousAuthor(String name) {
        return new ConflictException(
                "Không thể xác định tác giả '" + name
                        + "'. Hãy chọn author id hoặc bổ sung ngày sinh, bút danh hay quốc tịch"
        );
    }

    private void enrichAuthor(
            Author author,
            AuthorRequest request,
            String penName,
            String nationality
    ) {
        if (author.getPenName() == null) author.setPenName(penName);
        if (author.getBirthDate() == null) author.setBirthDate(request.birthDate());
        if (author.getDeathDate() == null) author.setDeathDate(request.deathDate());
        if (author.getNationality() == null) author.setNationality(nationality);
    }

    private void validateAuthorDates(AuthorRequest request) {
        if (request.birthDate() != null
                && request.deathDate() != null
                && request.deathDate().isBefore(request.birthDate())) {
            throw new BadRequestException("Ngày mất của tác giả không được trước ngày sinh");
        }
    }

    private boolean hasIdentityDetails(AuthorRequest request) {
        return request.birthDate() != null
                || normalizeNullable(request.penName()) != null
                || normalizeNullable(request.nationality()) != null;
    }

    private boolean compatibleText(String requested, String existing) {
        if (requested == null || existing == null) {
            return true;
        }
        return requested.equalsIgnoreCase(normalizeNullable(existing));
    }

    private boolean exactText(String requested, String existing) {
        return requested == null || requested.equalsIgnoreCase(normalizeNullable(existing));
    }

    private Category resolveCategory(String rawCategory) {
        String name = normalizeRequired(rawCategory, "Thể loại");
        return categoryRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    String slug = uniqueCategorySlug(slugify(name));
                    return categoryRepository.save(new Category(name, slug));
                });
    }

    private String uniqueCategorySlug(String initialSlug) {
        if (categoryRepository.findBySlugIgnoreCase(initialSlug).isEmpty()) {
            return initialSlug;
        }
        return initialSlug + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private Publisher resolvePublisher(String rawPublisher, PublisherRequest details) {
        if (details != null && details.id() != null) {
            return publisherRepository.findById(details.id())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy nhà xuất bản có id " + details.id()
                    ));
        }

        String name = details == null
                ? normalizeNullable(rawPublisher)
                : normalizeRequired(details.name(), "Tên nhà xuất bản");
        if (name == null) {
            return null;
        }
        Publisher publisher = publisherRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> publisherRepository.save(new Publisher(name)));
        if (details != null) {
            if (publisher.getAddress() == null) {
                publisher.setAddress(normalizeNullable(details.address()));
            }
            if (publisher.getEmail() == null) {
                publisher.setEmail(normalizeNullable(details.email()));
            }
            if (publisher.getPhone() == null) {
                publisher.setPhone(normalizeNullable(details.phone()));
            }
        }
        return publisher;
    }

    private void ensureIsbnAvailable(String isbn, Long currentBookId) {
        if (isbn == null) {
            return;
        }
        boolean exists = currentBookId == null
                ? bookRepository.existsByIsbn(isbn)
                : bookRepository.existsByIsbnAndIdNot(isbn, currentBookId);
        if (exists) {
            throw new ConflictException("ISBN đã được sử dụng cho sách khác");
        }
    }

    private BookResponse toResponse(Book book) {
        long quantity = bookCopyRepository.countByBookId(book.getId());
        long availableQuantity = bookCopyRepository.countByBookIdAndStatus(
                book.getId(),
                BookCopyStatus.AVAILABLE
        );
        return BookResponse.from(book, quantity, availableQuantity);
    }

    private String normalizeRequired(String value, String fieldName) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new BadRequestException(fieldName + " không được để trống");
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.strip().replaceAll("\\s+", " ");
        return normalized.isEmpty() ? null : normalized;
    }

    static String slugify(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        return normalized.isEmpty() ? "category" : normalized;
    }
}
