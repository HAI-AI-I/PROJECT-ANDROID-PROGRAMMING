"use client";

import { useCallback, useEffect, useState } from "react";
import { Search } from "lucide-react";
import Select from "@/components/ui/Select";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import Pagination from "@/components/ui/Pagination";
import Table from "@/components/ui/Table";
import { getHistoryActionBadge } from "@/components/ui/Badge";
import { historyService } from "@/services/historyService";
import type { HistoryRecord } from "@/types/History";
import pageStyles from "@/styles/page.module.scss";

const PAGE_SIZE = 10;

export default function HistoryContent() {
  const [records, setRecords] = useState<HistoryRecord[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [search, setSearch] = useState("");
  const [action, setAction] = useState("");

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const result = await historyService.getHistory({
        search,
        action: action || undefined,
        page,
        pageSize: PAGE_SIZE,
      });
      setRecords(result.items);
      setTotal(result.total);
      setTotalPages(Math.max(result.totalPages, 1));
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [search, action, page]);

  useEffect(() => { fetchData(); }, [fetchData]);

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Lịch sử mượn/trả</h2></div>
      <div className={pageStyles.toolbar}>
        <div className={pageStyles.searchWrap}>
          <Search size={16} color="var(--text-tertiary)" />
          <input
            placeholder="Tìm người dùng, sách hoặc mã đơn..."
            value={search}
            onChange={(event) => { setSearch(event.target.value); setPage(1); }}
          />
        </div>
        <Select
          className={pageStyles.filterSelect}
          value={action}
          onChange={(event) => { setAction(event.target.value); setPage(1); }}
          placeholder="Loại hoạt động"
          options={[
            { value: "request", label: "Tạo yêu cầu" },
            { value: "borrow", label: "Nhận sách" },
            { value: "return", label: "Trả sách" },
            { value: "cancel_request", label: "Hủy yêu cầu" },
          ]}
        />
      </div>
      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : records.length === 0 ? (
          <EmptyState title="Không có lịch sử phù hợp" />
        ) : (
          <>
            <Table columns={[
              { key: "action", header: "Loại", render: (record) => getHistoryActionBadge(record.action) },
              { key: "referenceCode", header: "Mã đơn" },
              { key: "userName", header: "Người dùng" },
              { key: "bookTitle", header: "Sách" },
              { key: "description", header: "Mô tả" },
              { key: "date", header: "Thời gian" },
            ]} data={records} keyExtractor={(record) => record.id} />
            <Pagination page={page} totalPages={totalPages} total={total} pageSize={PAGE_SIZE} onPageChange={setPage} />
          </>
        )}
      </div>
    </>
  );
}
