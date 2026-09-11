"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import BookForm from "./BookForm";
import EmptyState from "@/components/ui/EmptyState";
import Button from "@/components/ui/Button";
import { bookService } from "@/services/bookService";
import type { Book } from "@/types/Book";

export default function BookEditPage() {
  const params = useParams();
  const id = params.id as string;
  const [book, setBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    bookService.getBookById(id).then((data) => {
      setBook(data);
      setLoading(false);
    });
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
        description="Không thể sửa sách không tồn tại."
        action={
          <Link href="/admin/books">
            <Button variant="secondary">Quay lại danh sách</Button>
          </Link>
        }
      />
    );
  }

  return <BookForm mode="edit" initialData={book} />;
}
