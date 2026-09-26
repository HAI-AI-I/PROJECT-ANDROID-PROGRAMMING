"use client";

import { useCallback, useEffect, useState } from "react";
import { CheckCheck, Plus, Trash2 } from "lucide-react";
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
  info: "Thông tin",
  warning: "Cảnh báo",
  success: "Thành công",
  error: "Khẩn cấp",
};

const typeVariants: Record<NotificationType, "default" | "warning" | "success" | "danger"> = {
  info: "default",
  warning: "warning",
  success: "success",
  error: "danger",
};

export default function NotificationsContent() {
  const { showToast } = useToast();
  const [items, setItems] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<Notification | null>(null);
  const [form, setForm] = useState<NotificationFormData>({ title: "", message: "", type: "info" });

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      setItems(await notificationService.getNotifications());
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleBroadcast = async () => {
    if (!form.title.trim() || !form.message.trim()) {
      showToast("Vui lòng nhập đầy đủ tiêu đề và nội dung", "error");
      return;
    }
    setProcessing(true);
    try {
      const recipientCount = await notificationService.broadcast(form);
      showToast(`Đã gửi thông báo cho ${recipientCount} độc giả`);
      setForm({ title: "", message: "", type: "info" });
      setShowForm(false);
    } catch (requestError) {
      showToast(requestError instanceof Error ? requestError.message : "Không thể gửi thông báo", "error");
    } finally {
      setProcessing(false);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      await notificationService.deleteNotification(deleteTarget.id);
      showToast("Xóa thông báo thành công");
      setDeleteTarget(null);
      await fetchData();
    } catch (requestError) {
      showToast(requestError instanceof Error ? requestError.message : "Không thể xóa thông báo", "error");
    }
  };

  const handleReadAll = async () => {
    try {
      await notificationService.markAllAsRead();
      showToast("Đã đánh dấu tất cả là đã đọc");
      await fetchData();
    } catch (requestError) {
      showToast(requestError instanceof Error ? requestError.message : "Không thể cập nhật thông báo", "error");
    }
  };

  return (
    <>
      <div className={pageStyles.pageHeader}>
        <h2 className={pageStyles.pageTitle}>Thông báo của quản trị viên</h2>
        <div className={styles.topActions}>
          <Button variant="secondary" size="sm" onClick={handleReadAll}>
            <CheckCheck size={16} />Đọc tất cả
          </Button>
          <Button size="sm" onClick={() => setShowForm((visible) => !visible)}>
            <Plus size={16} />Gửi thông báo
          </Button>
        </div>
      </div>

      {showForm && (
        <div className={styles.form}>
          <p className={styles.formTitle}>Gửi thông báo đến toàn bộ độc giả đang hoạt động</p>
          <div className={styles.formGrid}>
            <Input label="Tiêu đề" maxLength={200} value={form.title} onChange={(event) => setForm({ ...form, title: event.target.value })} />
            <Input label="Nội dung" maxLength={1000} value={form.message} onChange={(event) => setForm({ ...form, message: event.target.value })} />
            <Select label="Mức độ" value={form.type} onChange={(event) => setForm({ ...form, type: event.target.value as NotificationType })} options={
              Object.entries(typeLabels).map(([value, label]) => ({ value, label }))
            } />
          </div>
          <div className={styles.formActions}>
            <Button variant="secondary" onClick={() => setShowForm(false)}>Hủy</Button>
            <Button onClick={handleBroadcast} disabled={processing}>{processing ? "Đang gửi..." : "Gửi cho độc giả"}</Button>
          </div>
        </div>
      )}

      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : items.length === 0 ? (
          <EmptyState title="Tài khoản Admin chưa có thông báo" />
        ) : (
          <div className={styles.list}>
            {items.map((notification) => (
              <div key={notification.id} className={`${styles.item} ${!notification.isRead ? styles.unread : ""}`}>
                {!notification.isRead && <div className={styles.dot} />}
                <div className={styles.content}>
                  <p className={styles.title}>{notification.title}</p>
                  <p className={styles.message}>{notification.message}</p>
                  <div className={styles.meta}>
                    <Badge variant={typeVariants[notification.type]}>{typeLabels[notification.type]}</Badge>
                    <span className={styles.date}>{notification.createdDate}</span>
                    {!notification.isRead && <Badge variant="primary">Chưa đọc</Badge>}
                  </div>
                </div>
                <div className={styles.actions}>
                  {!notification.isRead && <Button variant="ghost" size="icon" title="Đánh dấu đã đọc" onClick={async () => { await notificationService.markAsRead(notification.id); await fetchData(); }}><CheckCheck size={16} /></Button>}
                  <Button variant="ghost" size="icon" onClick={() => setDeleteTarget(notification)}><Trash2 size={16} /></Button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <Modal open={!!deleteTarget} title="Xóa thông báo" confirmLabel="Xóa" variant="danger" onConfirm={handleDelete} onCancel={() => setDeleteTarget(null)}>
        Bạn có chắc muốn xóa thông báo <strong>{deleteTarget?.title}</strong> khỏi tài khoản Admin?
      </Modal>
    </>
  );
}
