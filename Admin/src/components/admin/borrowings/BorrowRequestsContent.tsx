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
  const [rejectTarget, setRejectTarget] = useState<BorrowRequest | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true); setError(false);
    try {
      setRequests(await borrowingService.getBorrowRequests());
    } catch { setError(true); } finally { setLoading(false); }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleApprove = async () => {
    if (!approveTarget) return;
    await borrowingService.approveRequest(approveTarget.id);
    showToast("Duyệt yêu cầu thành công");
    setApproveTarget(null);
    fetchData();
  };

  const handleReject = async () => {
    if (!rejectTarget) return;
    await borrowingService.rejectRequest(rejectTarget.id);
    showToast("Đã từ chối yêu cầu");
    setRejectTarget(null);
    fetchData();
  };

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Yêu cầu mượn sách</h2></div>
      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : requests.length === 0 ? (
          <EmptyState title="Không có yêu cầu mượn" description="Tất cả yêu cầu đã được xử lý." />
        ) : (
          <Table
            columns={[
              { key: "id", header: "Mã yêu cầu" },
              { key: "readerName", header: "Độc giả" },
              { key: "bookTitle", header: "Sách" },
              { key: "requestDate", header: "Ngày yêu cầu" },
              { key: "status", header: "Trạng thái", render: (req) => getBorrowingStatusBadge(req.status) },
              { key: "actions", header: "Thao tác", sortable: false, render: (req) => (
                <div style={{ display: "flex", gap: 8 }}>
                  <Button variant="secondary" size="sm" onClick={() => setRejectTarget(req)}>Từ chối</Button>
                  <Button size="sm" onClick={() => setApproveTarget(req)}>Duyệt</Button>
                </div>
              )},
            ]}
            data={requests}
            keyExtractor={(req) => req.id}
          />
        )}
      </div>
      <Modal open={!!approveTarget} title="Duyệt yêu cầu" onConfirm={handleApprove} onCancel={() => setApproveTarget(null)}>
        Bạn có chắc muốn duyệt yêu cầu mượn sách <strong>{approveTarget?.bookTitle}</strong> của <strong>{approveTarget?.readerName}</strong>?
      </Modal>
      <Modal open={!!rejectTarget} title="Từ chối yêu cầu" confirmLabel="Từ chối" variant="danger" onConfirm={handleReject} onCancel={() => setRejectTarget(null)}>
        Bạn có chắc muốn từ chối yêu cầu mượn sách <strong>{rejectTarget?.bookTitle}</strong>?
      </Modal>
    </>
  );
}
