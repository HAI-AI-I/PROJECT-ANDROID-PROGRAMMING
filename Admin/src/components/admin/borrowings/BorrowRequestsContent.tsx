"use client";

import { useCallback, useEffect, useState } from "react";
import Button from "@/components/ui/Button";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import Modal from "@/components/ui/Modal";
import Table from "@/components/ui/Table";
import { getBorrowingStatusBadge } from "@/components/ui/Badge";
import { useToast } from "@/components/ui/Toast";
import { borrowingService } from "@/services/borrowingService";
import type { BorrowRequest } from "@/types/Borrowing";
import pageStyles from "@/styles/page.module.scss";

export default function BorrowRequestsContent() {
  const { showToast } = useToast();
  const [requests, setRequests] = useState<BorrowRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [approveTarget, setApproveTarget] = useState<BorrowRequest | null>(null);
  const [processing, setProcessing] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true); setError(false);
    try {
      setRequests(await borrowingService.getBorrowRequests());
    } catch { setError(true); } finally { setLoading(false); }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleApprove = async () => {
    if (!approveTarget) return;
    setProcessing(true);
    try {
      await borrowingService.approveRequest(approveTarget.id);
      showToast("Đã xác nhận giao sách cho độc giả");
      setApproveTarget(null);
      await fetchData();
    } catch (requestError) {
      showToast(requestError instanceof Error ? requestError.message : "Không thể giao sách", "error");
    } finally {
      setProcessing(false);
    }
  };

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Yêu cầu nhận sách</h2></div>
      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : requests.length === 0 ? (
          <EmptyState title="Không có yêu cầu mượn" description="Tất cả yêu cầu đã được xử lý." />
        ) : (
          <Table
            columns={[
              { key: "id", header: "Mã yêu cầu" },
              { key: "readerName", header: "Độc giả" },
              { key: "bookTitle", header: "Sách" },
              { key: "copyBarcode", header: "Mã bản sách" },
              { key: "requestDate", header: "Ngày yêu cầu" },
              { key: "status", header: "Trạng thái", render: (req) => getBorrowingStatusBadge(req.status) },
              { key: "actions", header: "Thao tác", sortable: false, render: (req) => (
                <Button size="sm" onClick={() => setApproveTarget(req)}>Xác nhận giao</Button>
              )},
            ]}
            data={requests}
            keyExtractor={(req) => req.id}
          />
        )}
      </div>
      <Modal open={!!approveTarget} title="Xác nhận giao sách" confirmLabel={processing ? "Đang xử lý..." : "Xác nhận giao"} onConfirm={handleApprove} onCancel={() => setApproveTarget(null)}>
        Xác nhận đã giao bản sách <strong>{approveTarget?.copyBarcode}</strong> của sách <strong>{approveTarget?.bookTitle}</strong> cho <strong>{approveTarget?.readerName}</strong>?
      </Modal>
    </>
  );
}
