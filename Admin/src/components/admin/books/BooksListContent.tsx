"use client";

import { useCallback, useEffect, useState } from "react";
import Link from "next/link";
import { Plus, Search } from "lucide-react";
import BookTable from "./BookTable";
import Button from "@/components/ui/Button";
import Select from "@/components/ui/Select";
import Pagination from "@/components/ui/Pagination";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import Modal from "@/components/ui/Modal";
import { useToast } from "@/components/ui/Toast";
import { bookCategories } from "@/data/books";
import { bookService } from "@/services/bookService";
import type { Book } from "@/types/Book";
import styles from "./BooksListContent.module.scss";

const PAGE_SIZE = 8;

export default function BooksListContent() {
  const { showToast } = useToast();
  const [books, setBooks] = useState<Book[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");
  const [status, setStatus] = useState("");
  const [deleteTarget, setDeleteTarget] = useState<Book | null>(null);
  const [selectedBookIds, setSelectedBookIds] = useState<string[]>([]);
  const [bulkDeleteOpen, setBulkDeleteOpen] = useState(false);

  const fetchBooks = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const result = await bookService.getBooks({
        search,
        category: category || undefined,
        status: status || undefined,
        page,
        pageSize: PAGE_SIZE,
      });
      setBooks(result.items);
      setTotal(result.total);
      setTotalPages(result.totalPages);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [search, category, status, page]);

  useEffect(() => {
    fetchBooks();
  }, [fetchBooks]);

  const handleSearchChange = (value: string) => {
    setSearch(value);
    setPage(1);
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    const ok = await bookService.deleteBook(deleteTarget.bookId);
    if (ok) {
      showToast("Xóa sách thành công");
      setDeleteTarget(null);
      fetchBooks();
    } else {
      showToast("Không thể xóa sách", "error");
    }
  };

  const handleBulkDelete = async () => {
    await Promise.all(selectedBookIds.map((id) => bookService.deleteBook(id)));
    showToast(`Đã xóa ${selectedBookIds.length} sách`);
    setSelectedBookIds([]);
    setBulkDeleteOpen(false);
    fetchBooks();
  };

  return (
    <>
      <div className={styles.header}>
        <h2 className={styles.title}>Quản lý sách</h2>
        <div style={{ display: "flex", gap: 8 }}>
          {selectedBookIds.length > 0 && (
            <Button variant="danger" onClick={() => setBulkDeleteOpen(true)}>
              Xóa {selectedBookIds.length} sách
            </Button>
          )}
          <Link href="/admin/books/new"><Button><Plus size={16} />Thêm sách</Button></Link>
        </div>
      </div>

      <div className={styles.toolbar}>
        <div className={styles.searchWrap}>
          <Search size={16} color="var(--text-tertiary)" />
          <input
            type="text"
            placeholder="Tìm kiếm sách..."
            value={search}
            onChange={(e) => handleSearchChange(e.target.value)}
          />
        </div>
        <div className={styles.filters}>
          <Select
            className={styles.filterSelect}
            value={category}
            onChange={(e) => {
              setCategory(e.target.value);
              setPage(1);
            }}
            placeholder="Thể loại"
            options={bookCategories.map((c) => ({ value: c, label: c }))}
          />
          <Select
            className={styles.filterSelect}
            value={status}
            onChange={(e) => {
              setStatus(e.target.value);
              setPage(1);
            }}
            placeholder="Trạng thái"
            options={[
              { value: "available", label: "Có sẵn" },
              { value: "borrowed", label: "Đang được mượn" },
              { value: "out_of_stock", label: "Hết sách" },
            ]}
          />
        </div>
      </div>

      <div className={styles.card}>
        {loading ? (
          <LoadingState />
        ) : error ? (
          <ErrorState onRetry={fetchBooks} />
        ) : books.length === 0 ? (
          <EmptyState
            title="Không tìm thấy sách"
            description="Thử thay đổi bộ lọc hoặc thêm sách mới."
            action={
              <Link href="/admin/books/new">
                <Button size="sm">Thêm sách</Button>
              </Link>
            }
          />
        ) : (
          <>
            <BookTable books={books} onDelete={setDeleteTarget} onSelectionChange={setSelectedBookIds} />
            <Pagination
              page={page}
              totalPages={totalPages}
              total={total}
              pageSize={PAGE_SIZE}
              onPageChange={setPage}
            />
          </>
        )}
      </div>

      <Modal
        open={!!deleteTarget}
        title="Xóa sách"
        confirmLabel="Xóa"
        variant="danger"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      >
        Bạn có chắc muốn xóa sách <strong>{deleteTarget?.title}</strong>? Hành
        động này không thể hoàn tác.
      </Modal>
      <Modal
        open={bulkDeleteOpen}
        title="Xóa nhiều sách"
        confirmLabel="Xóa tất cả"
        variant="danger"
        onConfirm={handleBulkDelete}
        onCancel={() => setBulkDeleteOpen(false)}
      >
        Bạn có chắc muốn xóa <strong>{selectedBookIds.length} sách</strong> đã chọn? Hành động này không thể hoàn tác.
      </Modal>
    </>
  );
}
