"use client";

import { useCallback, useEffect, useState } from "react";
import { Search } from "lucide-react";
import Select from "@/components/ui/Select";
import Pagination from "@/components/ui/Pagination";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import Table from "@/components/ui/Table";
import { getBorrowingStatusBadge } from "@/components/ui/Badge";
import { borrowingService } from "@/services/borrowingService";
import type { Borrowing } from "@/types/Borrowing";
import pageStyles from "@/styles/page.module.scss";

const PAGE_SIZE = 8;

export default function BorrowingsListContent() {
  const [items, setItems] = useState<Borrowing[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");

  const fetchData = useCallback(async () => {
    setLoading(true); setError(false);
    try {
      const r = await borrowingService.getBorrowings({ search, status: status || undefined, page, pageSize: PAGE_SIZE });
      setItems(r.items); setTotal(r.total); setTotalPages(r.totalPages);
    } catch { setError(true); } finally { setLoading(false); }
  }, [search, status, page]);

  useEffect(() => { fetchData(); }, [fetchData]);

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Quản lý mượn sách</h2></div>
      <div className={pageStyles.toolbar}>
        <div className={pageStyles.searchWrap}>
          <Search size={16} color="var(--text-tertiary)" />
          <input placeholder="Tìm mã phiếu, độc giả, sách..." value={search} onChange={(e) => { setSearch(e.target.value); setPage(1); }} />
        </div>
        <Select className={pageStyles.filterSelect} value={status} onChange={(e) => { setStatus(e.target.value); setPage(1); }} placeholder="Trạng thái"
          options={[
            { value: "borrowing", label: "Đang mượn" }, { value: "returned", label: "Đã trả" },
            { value: "overdue", label: "Quá hạn" }, { value: "pending", label: "Chờ duyệt" },
          ]} />
      </div>
      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : items.length === 0 ? (
          <EmptyState title="Không có phiếu mượn" />
        ) : (
          <>
            <Table columns={[
              { key: "id", header: "Mã phiếu" },
              { key: "readerName", header: "Độc giả" },
              { key: "bookTitle", header: "Sách" },
              { key: "borrowDate", header: "Ngày mượn" },
              { key: "dueDate", header: "Hạn trả" },
              { key: "status", header: "Trạng thái", render: (b) => getBorrowingStatusBadge(b.status) },
            ]} data={items} keyExtractor={(b) => b.id} />
            <Pagination page={page} totalPages={totalPages} total={total} pageSize={PAGE_SIZE} onPageChange={setPage} />
          </>
        )}
      </div>
    </>
  );
}
