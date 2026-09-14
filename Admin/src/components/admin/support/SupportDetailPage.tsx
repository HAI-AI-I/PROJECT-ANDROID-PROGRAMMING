"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import Button from "@/components/ui/Button";
import LoadingState from "@/components/ui/LoadingState";
import EmptyState from "@/components/ui/EmptyState";
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
  const [reply, setReply] = useState("");

  useEffect(() => {
    supportService.getRequestById(id).then((d) => { setRequest(d); if (d?.reply) setReply(d.reply); setLoading(false); });
  }, [id]);

  const refresh = async () => {
    const d = await supportService.getRequestById(id);
    setRequest(d);
  };

  if (loading) return <LoadingState />;
  if (!request) return <EmptyState title="Không tìm thấy yêu cầu" action={<Link href="/admin/support"><Button variant="secondary">Quay lại</Button></Link>} />;

  return (
    <div className={styles.detail}>
      <div style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 16 }}>
        <h2 style={{ fontSize: 20, fontWeight: 700 }}>{request.subject}</h2>
        {getSupportStatusBadge(request.status)}
      </div>
      <div style={{ fontSize: 14, color: "var(--text-secondary)" }}>
        <p><strong>Người gửi:</strong> {request.userName} ({request.userEmail})</p>
        <p><strong>Ngày gửi:</strong> {request.createdDate}</p>
      </div>
      <div className={styles.message}>{request.message}</div>

      {request.reply && request.status !== "open" && (
        <div>
          <p style={{ fontSize: 14, fontWeight: 600, marginBottom: 8 }}>Phản hồi:</p>
          <div className={styles.message}>{request.reply}</div>
        </div>
      )}

      {request.status !== "closed" && request.status !== "resolved" && (
        <div className={styles.replyBox}>
          <p style={{ fontSize: 14, fontWeight: 600, marginBottom: 8 }}>Trả lời</p>
          <textarea placeholder="Nhập phản hồi..." value={reply} onChange={(e) => setReply(e.target.value)} />
        </div>
      )}

      <div className={styles.actions}>
        {request.status !== "closed" && request.status !== "resolved" && (
          <>
            <Button onClick={async () => {
              if (!reply.trim()) return;
              await supportService.reply(id, reply);
              showToast("Đã gửi phản hồi");
              refresh();
            }}>Gửi phản hồi</Button>
            <Button variant="secondary" onClick={async () => {
              await supportService.markResolved(id);
              showToast("Đã đánh dấu xử lý");
              refresh();
            }}>Đánh dấu đã xử lý</Button>
          </>
        )}
        {request.status !== "closed" && (
          <Button variant="danger" onClick={async () => {
            await supportService.closeRequest(id);
            showToast("Đã đóng yêu cầu");
            refresh();
          }}>Đóng request</Button>
        )}
        <Link href="/admin/support"><Button variant="ghost">Quay lại</Button></Link>
      </div>
    </div>
  );
}
