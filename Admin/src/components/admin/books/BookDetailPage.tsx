"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import BookDetail from "./BookDetail";
import EmptyState from "@/components/ui/EmptyState";
import Button from "@/components/ui/Button";
import Link from "next/link";
import { bookService } from "@/services/bookService";
import type { Book, BookBorrowHistory } from "@/types/Book";

export default function BookDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const [book, setBook] = useState<Book | null>(null);
  const [history, setHistory] = useState<BookBorrowHistory[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      const [bookData, historyData] = await Promise.all([
        bookService.getBookById(id),
        bookService.getBookHistory(id),
      ]);
      setBook(bookData);
      setHistory(historyData);
      setLoading(false);
    }
    load();
  }, [id]);

  if (loading) {
    return (
      <p style={{ textAlign: "center", padding: 48, color: "var(--text-secondary)" }}>
        Đang tải...
      </p>
    );
  }

  if (!book) {
    return (
      <EmptyState
        title="Không tìm thấy sách"
        description="Sách này có thể đã bị xóa hoặc không tồn tại."
        action={
          <Link href="/admin/books">
            <Button variant="secondary">Quay lại danh sách</Button>
          </Link>
        }
      />
    );
  }

  return <BookDetail book={book} history={history} />;
}
