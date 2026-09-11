"use client";

import { useEffect, useState } from "react";
import Button from "@/components/ui/Button";
import Table from "@/components/ui/Table";
import Modal from "@/components/ui/Modal";
import EmptyState from "@/components/ui/EmptyState";
import { getBorrowingStatusBadge } from "@/components/ui/Badge";
import { useToast } from "@/components/ui/Toast";
import { borrowingService } from "@/services/borrowingService";
import type { Borrowing } from "@/types/Borrowing";
import pageStyles from "@/styles/page.module.scss";
import styles from "./ReturnsContent.module.scss";

export default function ReturnsContent() {
  const { showToast } = useToast();
  const [query, setQuery] = useState("");
  const [result, setResult] = useState<Borrowing | null>(null);
  const [searched, setSearched] = useState(false);
  const [loading, setLoading] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [recentBorrowings, setRecentBorrowings] = useState<Borrowing[]>([]);

  const fetchRecentBorrowings = async () => {
    const data = await borrowingService.getRecentBorrowings();
    setRecentBorrowings(data.filter((item) => item.status === "borrowing" || item.status === "overdue"));
  };

  useEffect(() => { fetchRecentBorrowings(); }, []);

  const handleSearch = async () => {
    if (!query.trim()) return;
    setLoading(true);
    const data = await borrowingService.searchForReturn(query);
    setResult(data);
    setSearched(true);
    setLoading(false);
  };

  const handleConfirm = async () => {
    if (!result) return;
    await borrowingService.confirmReturn(result.id);
    showToast("Xác nhận trả sách thành công");
    setConfirmOpen(false);
    setResult(null);
    setQuery("");
    setSearched(false);
    fetchRecentBorrowings();
  };

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Trả sách</h2></div>

      <div className={styles.searchSection}>
        <p style={{ fontSize: 14, color: "var(--text-secondary)", marginBottom: 12 }}>Tìm phiếu mượn</p>
        <div className={styles.searchRow}>
          <input placeholder="Nhập mã phiếu / mã độc giả" value={query} onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()} />
          <Button onClick={handleSearch} disabled={loading}>{loading ? "Đang tìm..." : "Tìm kiếm"}</Button>
        </div>
      </div>

      {searched && !result && (
        <EmptyState title="Không tìm thấy phiếu mượn" description="Kiểm tra lại mã phiếu hoặc mã độc giả." />
      )}

      {result && (
        <div className={styles.result}>
          <p className={styles.resultTitle}>Thông tin phiếu mượn</p>
          <div className={styles.row}><span className={styles.label}>Mã phiếu</span><span className={styles.value}>{result.id}</span></div>
          <div className={styles.row}><span className={styles.label}>Độc giả</span><span className={styles.value}>{result.readerName}</span></div>
          <div className={styles.row}><span className={styles.label}>Sách</span><span className={styles.value}>{result.bookTitle}</span></div>
          <div className={styles.row}><span className={styles.label}>Ngày mượn</span><span className={styles.value}>{result.borrowDate}</span></div>
          <div className={styles.row}><span className={styles.label}>Hạn trả</span><span className={styles.value}>{result.dueDate}</span></div>
          <div className={styles.row}><span className={styles.label}>Trạng thái</span><span>{getBorrowingStatusBadge(result.status)}</span></div>
          {result.overdueDays && (
            <>
              <div className={styles.row}><span className={styles.label}>Quá hạn</span><span className={styles.fine}>{result.overdueDays} ngày</span></div>
              <div className={styles.row}><span className={styles.label}>Tiền phạt</span><span className={styles.fine}>{result.fineAmount?.toLocaleString("vi-VN")}đ</span></div>
            </>
          )}
          <div className={styles.confirmBtn}>
            <Button onClick={() => setConfirmOpen(true)}>Xác nhận trả sách</Button>
          </div>
        </div>
      )}

      <div className={pageStyles.card}>
        <div className={styles.recentHeader}>
          <h3>Phiếu đang chờ trả</h3>
          <span>{recentBorrowings.length} phiếu</span>
        </div>
        <Table
          columns={[
            { key: "id", header: "Mã phiếu" },
            { key: "readerName", header: "Độc giả" },
            { key: "bookTitle", header: "Sách" },
            { key: "dueDate", header: "Hạn trả" },
            { key: "status", header: "Trạng thái", render: (item) => getBorrowingStatusBadge(item.status) },
          ]}
          data={recentBorrowings}
          keyExtractor={(item) => item.id}
        />
      </div>

      <Modal open={confirmOpen} title="Xác nhận trả sách" onConfirm={handleConfirm} onCancel={() => setConfirmOpen(false)}>
        Xác nhận trả sách <strong>{result?.bookTitle}</strong> của <strong>{result?.readerName}</strong>?
        {result?.fineAmount ? ` Tiền phạt: ${result.fineAmount.toLocaleString("vi-VN")}đ.` : ""}
      </Modal>
    </>
  );
}
