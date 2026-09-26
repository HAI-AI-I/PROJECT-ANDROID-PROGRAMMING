"use client";

import { useEffect, useState } from "react";
import Button from "@/components/ui/Button";
import Table from "@/components/ui/Table";
import Modal from "@/components/ui/Modal";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
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
  const [refundOpen, setRefundOpen] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [recentBorrowings, setRecentBorrowings] = useState<Borrowing[]>([]);
  const [queueLoading, setQueueLoading] = useState(true);
  const [queueError, setQueueError] = useState(false);
  const [searchError, setSearchError] = useState("");

  const fetchRecentBorrowings = async () => {
    setQueueLoading(true);
    setQueueError(false);
    try {
      setRecentBorrowings(await borrowingService.getReturnQueue());
    } catch {
      setQueueError(true);
    } finally {
      setQueueLoading(false);
    }
  };

  useEffect(() => { fetchRecentBorrowings(); }, []);

  const handleSearch = async () => {
    if (!query.trim()) return;
    setLoading(true);
    setSearchError("");
    try {
      setResult(await borrowingService.searchForReturn(query));
    } catch (requestError) {
      setResult(null);
      setSearchError(requestError instanceof Error ? requestError.message : "Không tìm thấy đơn mượn");
    } finally {
      setSearched(true);
      setLoading(false);
    }
  };

  const handleConfirm = async () => {
    if (!result) return;
    setProcessing(true);
    try {
      const updated = await borrowingService.confirmReturn(result.id);
      setResult(updated);
      showToast("Xác nhận trả sách thành công");
      setConfirmOpen(false);
      await fetchRecentBorrowings();
    } catch (requestError) {
      showToast(requestError instanceof Error ? requestError.message : "Không thể xác nhận trả sách", "error");
    } finally {
      setProcessing(false);
    }
  };

  const handleRefund = async () => {
    if (!result) return;
    setProcessing(true);
    try {
      const updated = await borrowingService.confirmDepositRefund(result.id);
      setResult(updated);
      setRefundOpen(false);
      showToast("Đã xác nhận hoàn tiền cọc");
      await fetchRecentBorrowings();
    } catch (requestError) {
      showToast(requestError instanceof Error ? requestError.message : "Không thể hoàn tiền cọc", "error");
    } finally {
      setProcessing(false);
    }
  };

  const formatMoney = (amount?: number) => `${(amount ?? 0).toLocaleString("vi-VN")}đ`;

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Trả sách</h2></div>

      <div className={styles.searchSection}>
        <p style={{ fontSize: 14, color: "var(--text-secondary)", marginBottom: 12 }}>Tìm phiếu mượn</p>
        <div className={styles.searchRow}>
          <input placeholder="Nhập mã đơn hoặc mã bản sách" value={query} onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()} />
          <Button onClick={handleSearch} disabled={loading}>{loading ? "Đang tìm..." : "Tìm kiếm"}</Button>
        </div>
      </div>

      {searched && !result && (
        <EmptyState title="Không thể xử lý đơn mượn" description={searchError || "Kiểm tra lại mã đơn hoặc mã bản sách."} />
      )}

      {result && (
        <div className={styles.result}>
          <p className={styles.resultTitle}>Thông tin phiếu mượn</p>
          <div className={styles.row}><span className={styles.label}>Mã phiếu</span><span className={styles.value}>{result.id}</span></div>
          <div className={styles.row}><span className={styles.label}>Độc giả</span><span className={styles.value}>{result.readerName}</span></div>
          <div className={styles.row}><span className={styles.label}>Sách</span><span className={styles.value}>{result.bookTitle}</span></div>
          <div className={styles.row}><span className={styles.label}>Mã bản sách</span><span className={styles.value}>{result.copyBarcode}</span></div>
          <div className={styles.row}><span className={styles.label}>Ngày mượn</span><span className={styles.value}>{result.borrowDate}</span></div>
          <div className={styles.row}><span className={styles.label}>Hạn trả</span><span className={styles.value}>{result.dueDate}</span></div>
          <div className={styles.row}><span className={styles.label}>Trạng thái</span><span>{getBorrowingStatusBadge(result.status)}</span></div>
          {(result.overdueDays ?? 0) > 0 && <div className={styles.row}><span className={styles.label}>Quá hạn</span><span className={styles.fine}>{result.overdueDays} ngày</span></div>}
          <div className={styles.row}><span className={styles.label}>Phí mượn</span><span className={styles.value}>{formatMoney(result.borrowFee)}</span></div>
          <div className={styles.row}><span className={styles.label}>Tiền cọc</span><span className={styles.value}>{formatMoney(result.depositAmount)}</span></div>
          <div className={styles.row}><span className={styles.label}>Tổng đã trả</span><span className={styles.value}>{formatMoney(result.paidAmount)}</span></div>
          {result.status === "returned" && (
            <div className={styles.row}><span className={styles.label}>Tiền cần hoàn</span><span className={result.remainingRefundAmount ? styles.refund : styles.value}>{formatMoney(result.remainingRefundAmount)}</span></div>
          )}
          <div className={styles.confirmBtn}>
            {(result.status === "borrowing" || result.status === "overdue") && <Button onClick={() => setConfirmOpen(true)}>Xác nhận trả sách</Button>}
            {result.status === "returned" && !result.depositRefunded && (result.remainingRefundAmount ?? 0) > 0 && <Button onClick={() => setRefundOpen(true)}>Xác nhận hoàn tiền cọc</Button>}
            {result.status === "returned" && result.depositRefunded && <span className={styles.refunded}>Đã hoàn tiền cọc</span>}
          </div>
        </div>
      )}

      <div className={pageStyles.card}>
        <div className={styles.recentHeader}>
          <h3>Đơn cần trả sách hoặc hoàn cọc</h3>
          <span>{recentBorrowings.length} phiếu</span>
        </div>
        {queueLoading ? <LoadingState /> : queueError ? <ErrorState onRetry={fetchRecentBorrowings} /> : recentBorrowings.length === 0 ? <EmptyState title="Không có đơn cần xử lý" /> : <Table
          columns={[
            { key: "id", header: "Mã phiếu" },
            { key: "readerName", header: "Độc giả" },
            { key: "bookTitle", header: "Sách" },
            { key: "dueDate", header: "Hạn trả" },
            { key: "status", header: "Trạng thái", render: (item) => getBorrowingStatusBadge(item.status) },
            { key: "actions", header: "Thao tác", sortable: false, render: (item) => <Button size="sm" variant="secondary" onClick={() => { setQuery(item.id); setResult(item); setSearched(true); }}>Xử lý</Button> },
          ]}
          data={recentBorrowings}
          keyExtractor={(item) => item.id}
        />}
      </div>

      <Modal open={confirmOpen} title="Xác nhận trả sách" confirmLabel={processing ? "Đang xử lý..." : "Xác nhận trả"} onConfirm={handleConfirm} onCancel={() => setConfirmOpen(false)}>
        Xác nhận trả sách <strong>{result?.bookTitle}</strong> của <strong>{result?.readerName}</strong>?
      </Modal>
      <Modal open={refundOpen} title="Xác nhận hoàn tiền cọc" confirmLabel={processing ? "Đang xử lý..." : "Đã hoàn tiền"} onConfirm={handleRefund} onCancel={() => setRefundOpen(false)}>
        Xác nhận thủ thư đã hoàn <strong>{formatMoney(result?.remainingRefundAmount)}</strong> tiền cọc cho <strong>{result?.readerName}</strong>?
      </Modal>
    </>
  );
}
