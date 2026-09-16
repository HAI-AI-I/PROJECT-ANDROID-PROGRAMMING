"use client";

import Link from "next/link";
import { Pencil } from "lucide-react";
import Card from "@/components/ui/Card";
import Button from "@/components/ui/Button";
import Table from "@/components/ui/Table";
import { getBookStatusBadge, getBorrowingStatusBadge } from "@/components/ui/Badge";
import type { Book, BookBorrowHistory } from "@/types/Book";
import { getBookStatus } from "@/types/Book";
import styles from "./BookDetail.module.scss";

interface BookDetailProps {
  book: Book;
  history: BookBorrowHistory[];
}

export default function BookDetail({ book, history }: BookDetailProps) {
  return (
    <div className={styles.wrapper}>
      <div className={styles.top}>
        <div className={styles.cover}>
          {book.cover ? (
            <img src={book.cover} alt={book.title} />
          ) : (
            "Chưa có ảnh bìa"
          )}
        </div>

        <div className={styles.info}>
          <h2 className={styles.bookTitle}>{book.title}</h2>
          {getBookStatusBadge(getBookStatus(book))}

          <div className={styles.meta}>
            <div className={styles.metaItem}>
              <span className={styles.metaLabel}>Tác giả</span>
              <span className={styles.metaValue}>{book.author}</span>
            </div>
            <div className={styles.metaItem}>
              <span className={styles.metaLabel}>Thể loại</span>
              <span className={styles.metaValue}>{book.category}</span>
            </div>
            <div className={styles.metaItem}>
              <span className={styles.metaLabel}>Nhà xuất bản</span>
              <span className={styles.metaValue}>{book.publisher}</span>
            </div>
            <div className={styles.metaItem}>
              <span className={styles.metaLabel}>Năm xuất bản</span>
              <span className={styles.metaValue}>{book.publishYear}</span>
            </div>
            <div className={styles.metaItem}>
              <span className={styles.metaLabel}>Tổng số</span>
              <span className={styles.metaValue}>{book.quantity}</span>
            </div>
            <div className={styles.metaItem}>
              <span className={styles.metaLabel}>Số lượng còn</span>
              <span className={styles.metaValue}>{book.availableQuantity}</span>
            </div>
          </div>

          <div className={styles.actions}>
            <Link href={`/admin/books/${book.bookId}/edit`}>
              <Button variant="secondary">
                <Pencil size={16} />
                Sửa sách
              </Button>
            </Link>
            <Link href="/admin/books">
              <Button variant="ghost">Quay lại danh sách</Button>
            </Link>
          </div>
        </div>
      </div>

      <Card title="Lịch sử mượn sách">
        {history.length === 0 ? (
          <p style={{ color: "var(--text-secondary)", fontSize: 14 }}>
            Chưa có lượt mượn nào cho cuốn sách này.
          </p>
        ) : (
          <Table
            columns={[
              { key: "readerName", header: "Độc giả" },
              { key: "borrowDate", header: "Ngày mượn" },
              { key: "dueDate", header: "Hạn trả" },
              {
                key: "returnDate",
                header: "Ngày trả",
                render: (item) => item.returnDate ?? "—",
              },
              {
                key: "status",
                header: "Trạng thái",
                render: (item) => getBorrowingStatusBadge(item.status),
              },
            ]}
            data={history}
            keyExtractor={(item) => item.id}
          />
        )}
      </Card>
    </div>
  );
}
