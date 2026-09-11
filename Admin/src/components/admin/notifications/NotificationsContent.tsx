"use client";

import { useCallback, useEffect, useState } from "react";
import { Plus, Trash2, CheckCheck } from "lucide-react";
import Button from "@/components/ui/Button";
import Input from "@/components/ui/Input";
import Select from "@/components/ui/Select";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import Modal from "@/components/ui/Modal";
import Badge from "@/components/ui/Badge";
import { useToast } from "@/components/ui/Toast";
import { notificationService } from "@/services/notificationService";
import type { Notification, NotificationFormData, NotificationType } from "@/types/Notification";
import pageStyles from "@/styles/page.module.scss";
import styles from "./NotificationsContent.module.scss";

const typeLabels: Record<NotificationType, string> = {
  overdue: "Quá hạn",
  borrow_request: "Yêu cầu mượn",
  system: "Hệ thống",
};

export default function NotificationsContent() {
  const { showToast } = useToast();
  const [items, setItems] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Notification | null>(null);
  const [form, setForm] = useState<NotificationFormData>({ title: "", message: "", type: "system" });

  const fetchData = useCallback(async () => {
    setLoading(true); setError(false);
    try { setItems(await notificationService.getNotifications()); }
    catch { setError(true); } finally { setLoading(false); }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleCreate = async () => {
    if (!form.title.trim() || !form.message.trim()) return;
    await notificationService.createNotification(form);
    showToast("Tạo thông báo thành công");
    setForm({ title: "", message: "", type: "system" });
    setShowForm(false);
    fetchData();
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    await notificationService.deleteNotification(deleteTarget.id);
    showToast("Xóa thông báo thành công");
    setDeleteTarget(null);
    fetchData();
  };

  return (
    <>
      <div className={pageStyles.pageHeader}>
        <h2 className={pageStyles.pageTitle}>Thông báo</h2>
        <div className={styles.topActions}>
          <Button variant="secondary" size="sm" onClick={async () => { await notificationService.markAllAsRead(); showToast("Đã đánh dấu tất cả đã đọc"); fetchData(); }}>
            <CheckCheck size={16} />Đọc tất cả
          </Button>
          <Button size="sm" onClick={() => setShowForm(!showForm)}><Plus size={16} />Tạo thông báo</Button>
        </div>
      </div>

      {showForm && (
        <div className={styles.form}>
          <p className={styles.formTitle}>Tạo thông báo mới</p>
          <div className={styles.formGrid}>
            <Input label="Tiêu đề" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
            <Input label="Nội dung" value={form.message} onChange={(e) => setForm({ ...form, message: e.target.value })} />
            <Select label="Loại" value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value as NotificationType })}
              options={Object.entries(typeLabels).map(([v, l]) => ({ value: v, label: l }))} />
          </div>
          <div className={styles.formActions}>
            <Button variant="secondary" onClick={() => setShowForm(false)}>Hủy</Button>
            <Button onClick={handleCreate}>Tạo</Button>
          </div>
        </div>
      )}

      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : items.length === 0 ? (
          <EmptyState title="Không có thông báo" />
        ) : (
          <div className={styles.list}>
            {items.map((n) => (
              <div key={n.id} className={`${styles.item} ${!n.isRead ? styles.unread : ""}`}>
                {!n.isRead && <div className={styles.dot} />}
                <div className={styles.content}>
                  <p className={styles.title}>{n.title}</p>
                  <p className={styles.message}>{n.message}</p>
                  <div className={styles.meta}>
                    <Badge variant="default">{typeLabels[n.type]}</Badge>
                    <span className={styles.date}>{n.createdDate}</span>
                    {!n.isRead && <Badge variant="primary">Chưa đọc</Badge>}
                  </div>
                </div>
                <div className={styles.actions}>
                  {!n.isRead && (
                    <Button variant="ghost" size="icon" title="Đánh dấu đã đọc"
                      onClick={async () => { await notificationService.markAsRead(n.id); fetchData(); }}>
                      <CheckCheck size={16} />
                    </Button>
                  )}
                  <Button variant="ghost" size="icon" onClick={() => setDeleteTarget(n)}><Trash2 size={16} /></Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <Modal open={!!deleteTarget} title="Xóa thông báo" confirmLabel="Xóa" variant="danger" onConfirm={handleDelete} onCancel={() => setDeleteTarget(null)}>
        Bạn có chắc muốn xóa thông báo <strong>{deleteTarget?.title}</strong>?
      </Modal>
    </>
  );
}
