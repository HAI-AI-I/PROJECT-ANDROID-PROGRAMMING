"use client";

import { useCallback, useEffect, useState } from "react";
import { Search } from "lucide-react";
import Select from "@/components/ui/Select";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import Table from "@/components/ui/Table";
import { getHistoryActionBadge } from "@/components/ui/Badge";
import { historyService } from "@/services/historyService";
import type { HistoryRecord } from "@/types/History";
import pageStyles from "@/styles/page.module.scss";

export default function HistoryContent() {
  const [records, setRecords] = useState<HistoryRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [search, setSearch] = useState("");
  const [action, setAction] = useState("");

  const fetchData = useCallback(async () => {
    setLoading(true); setError(false);
    try {
      setRecords(await historyService.getHistory({ search, action: action || undefined }));
    } catch { setError(true); } finally { setLoading(false); }
  }, [search, action]);

  useEffect(() => { fetchData(); }, [fetchData]);

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Lịch sử mượn/trả</h2></div>
      <div className={pageStyles.toolbar}>
        <div className={pageStyles.searchWrap}>
          <Search size={16} color="var(--text-tertiary)" />
          <input placeholder="Tìm người dùng, sách..." value={search} onChange={(e) => setSearch(e.target.value)} />
        </div>
        <Select className={pageStyles.filterSelect} value={action} onChange={(e) => setAction(e.target.value)} placeholder="Loại hoạt động"
          options={[
            { value: "borrow", label: "Mượn" }, { value: "return", label: "Trả" },
            { value: "extend", label: "Gia hạn" }, { value: "fine", label: "Phạt" }, { value: "cancel_request", label: "Hủy yêu cầu" },
          ]} />
      </div>
      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : records.length === 0 ? (
          <EmptyState title="Không có lịch sử" />
        ) : (
          <Table columns={[
            { key: "action", header: "Loại", render: (r) => getHistoryActionBadge(r.action) },
            { key: "userName", header: "Người dùng" },
            { key: "bookTitle", header: "Sách" },
            { key: "description", header: "Mô tả" },
            { key: "date", header: "Ngày" },
          ]} data={records} keyExtractor={(r) => r.id} />
        )}
      </div>
    </>
  );
}
