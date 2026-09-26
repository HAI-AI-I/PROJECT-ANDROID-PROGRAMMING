"use client";

import { useCallback, useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import Button from "@/components/ui/Button";
import LoadingState from "@/components/ui/LoadingState";
import EmptyState from "@/components/ui/EmptyState";
import ErrorState from "@/components/ui/ErrorState";
import { getSupportStatusBadge } from "@/components/ui/Badge";
import { useToast } from "@/components/ui/Toast";
import { supportService } from "@/services/supportService";
import type { SupportRequest } from "@/types/SupportRequest";
import styles from "./SupportContent.module.scss";

export default function SupportDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const { showToast } = useToast();
  const [request, setRequest] = useState<SupportRequest | null>(null);
  const [loading, setLoading] = useState(true);
  const [loadError, setLoadError] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [reply, setReply] = useState("");

  const fetchRequest = useCallback(async () => {
    setLoading(true);
    setLoadError(false);
    try {
      const data = await supportService.getRequestById(id);
      setRequest(data);
      setReply(data.reply ?? "");
    } catch {
      setLoadError(true);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    void fetchRequest();
  }, [fetchRequest]);

  const runAction = async (
    action: () => Promise<SupportRequest>,
    successMessage: string,
  ) => {
    setProcessing(true);
    try {
      const updated = await action();
      setRequest(updated);
      setReply(updated.reply ?? "");
      showToast(successMessage);
    } catch {
      showToast("Không thể cập nhật yêu cầu hỗ trợ", "error");
    } finally {
      setProcessing(false);
    }
  };

  if (loading) return <LoadingState />;
  if (loadError) return <ErrorState onRetry={fetchRequest} />;
  if (!request) {
    return (
      <EmptyState
        title="Không tìm thấy yêu cầu"
        action={<Link href="/admin/support"><Button variant="secondary">Quay lại</Button></Link>}
      />
    );
  }

  const editable = request.status === "open" || request.status === "in_progress";

  return (
    <div className={styles.detail}>
      <div className={styles.detailHeader}>
        <h2>{request.subject}</h2>
        {getSupportStatusBadge(request.status)}
      </div>
      <div className={styles.meta}>
        <p><strong>Người gửi:</strong> {request.userName} ({request.userEmail})</p>
        <p><strong>Ngày gửi:</strong> {request.createdDate}</p>
        {request.bookTitle && <p><strong>Sách liên quan:</strong> {request.bookTitle}</p>}
      </div>
      <div className={styles.message}>{request.message}</div>

      {request.reply && (
        <div>
          <p className={styles.sectionTitle}>
            Phản hồi{request.repliedDate ? ` · ${request.repliedDate}` : ""}
          </p>
          <div className={styles.message}>{request.reply}</div>
        </div>
      )}

      {editable && (
        <div className={styles.replyBox}>
          <p className={styles.sectionTitle}>Trả lời độc giả</p>
          <textarea
            placeholder="Nhập nội dung phản hồi..."
            value={reply}
            maxLength={2000}
            onChange={(event) => setReply(event.target.value)}
          />
        </div>
      )}

      <div className={styles.actions}>
        {editable && (
          <>
            <Button
              disabled={processing || !reply.trim()}
              onClick={() => void runAction(
                () => supportService.reply(id, reply),
                "Đã gửi phản hồi cho độc giả",
              )}
            >
              Gửi phản hồi
            </Button>
            <Button
              variant="secondary"
              disabled={processing}
              onClick={() => void runAction(
                () => supportService.markResolved(id),
                "Đã đánh dấu yêu cầu là đã xử lý",
              )}
            >
              Đánh dấu đã xử lý
            </Button>
          </>
        )}
        {request.status !== "closed" && (
          <Button
            variant="danger"
            disabled={processing}
            onClick={() => void runAction(
              () => supportService.closeRequest(id),
              "Đã đóng yêu cầu hỗ trợ",
            )}
          >
            Đóng yêu cầu
          </Button>
        )}
        <Link href="/admin/support"><Button variant="ghost">Quay lại</Button></Link>
      </div>
    </div>
  );
}
