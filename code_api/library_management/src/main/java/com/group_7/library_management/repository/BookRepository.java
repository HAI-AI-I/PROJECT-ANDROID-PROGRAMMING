package com.group_7.library_management.repository;

import com.group_7.library_management.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Collection;
import java.util.List;
import java.time.Instant;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    Optional<Book> findByIsbnAndActiveTrue(String isbn);

    @EntityGraph(attributePaths = {"authors", "category", "publisher"})
    Optional<Book> findByIdAndActiveTrue(Long id);

    @EntityGraph(attributePaths = {"authors", "category", "publisher"})
    Page<Book> findAllByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"authors", "category", "publisher"})
    List<Book> findAllByIdInAndActiveTrue(Collection<Long> ids);

    @EntityGraph(attributePaths = {"authors", "category", "publisher"})
    @Query(
            value = """
                    select distinct b from Book b
                    left join b.authors a
                    where b.active = true
                      and (:keyword is null
                           or lower(b.title) like lower(concat('%', :keyword, '%'))
                           or lower(a.name) like lower(concat('%', :keyword, '%'))
                           or lower(b.isbn) like lower(concat('%', :keyword, '%')))
                      and (:categorySlug is null
                           or lower(b.category.slug) = lower(:categorySlug))
                    """,
            countQuery = """
                    select count(distinct b.id) from Book b
                    left join b.authors a
                    where b.active = true
                      and (:keyword is null
                           or lower(b.title) like lower(concat('%', :keyword, '%'))
                           or lower(a.name) like lower(concat('%', :keyword, '%'))
                           or lower(b.isbn) like lower(concat('%', :keyword, '%')))
                      and (:categorySlug is null
                           or lower(b.category.slug) = lower(:categorySlug))
                    """
    )
    Page<Book> searchActiveBooks(
            @Param("keyword") String keyword,
            @Param("categorySlug") String categorySlug,
            Pageable pageable
    );

    @Query(value = """
            select b.id as bookId,
                   coalesce(bor.borrow_count, 0) as borrowCount,
                   coalesce(fav.favorite_count, 0) as favoriteCount,
                   coalesce(noti.notification_click_count, 0) as notificationClickCount,
                   coalesce(bor.borrow_count, 0) * 5
                       + coalesce(fav.favorite_count, 0) * 2
                       + coalesce(noti.notification_click_count, 0) * 2 as popularityScore
            from books b
            left join (
                select bc.book_id, count(*) as borrow_count
                from borrow_records br
                join book_copies bc on bc.id = br.book_copy_id
                where br.borrowed_at >= :since and br.status <> 'CANCELLED'
                group by bc.book_id
            ) bor on bor.book_id = b.id
            left join (
                select bf.book_id, count(*) as favorite_count
                from book_favorites bf
                where bf.created_at >= :since
                group by bf.book_id
            ) fav on fav.book_id = b.id
            left join (
                select n.book_id, count(*) as notification_click_count
                from notifications n
                where n.clicked_at >= :since and n.book_id is not null
                group by n.book_id
            ) noti on noti.book_id = b.id
            where b.active = b'1'
            order by popularityScore desc, b.created_at desc, b.id desc
            """, nativeQuery = true)
    List<BookPopularityStatistics> findPopularBooksSince(
            @Param("since") Instant since,
            Pageable pageable
    );

    @Query(value = """
            select b.id as bookId,
                   coalesce(bor.borrow_count, 0) as borrowCount,
                   coalesce(fav.favorite_count, 0) as favoriteCount,
                   coalesce(noti.notification_click_count, 0) as notificationClickCount,
                   coalesce(bor.borrow_count, 0) * 5
                       + coalesce(fav.favorite_count, 0) * 2
                       + coalesce(noti.notification_click_count, 0) * 2 as popularityScore
            from books b
            left join (
                select bc.book_id, count(*) as borrow_count
                from borrow_records br
                join book_copies bc on bc.id = br.book_copy_id
                where br.borrowed_at >= :since and br.status <> 'CANCELLED'
                group by bc.book_id
            ) bor on bor.book_id = b.id
            left join (
                select bf.book_id, count(*) as favorite_count
                from book_favorites bf
                where bf.created_at >= :since
                group by bf.book_id
            ) fav on fav.book_id = b.id
            left join (
                select n.book_id, count(*) as notification_click_count
                from notifications n
                where n.clicked_at >= :since and n.book_id is not null
                group by n.book_id
            ) noti on noti.book_id = b.id
            where b.active = b'1'
              and b.category_id = :categoryId
              and b.id <> :excludedBookId
            order by popularityScore desc, b.created_at desc, b.id desc
            """, nativeQuery = true)
    List<BookPopularityStatistics> findRelatedPopularBooksSince(
            @Param("categoryId") Long categoryId,
            @Param("excludedBookId") Long excludedBookId,
            @Param("since") Instant since,
            Pageable pageable
    );
}
