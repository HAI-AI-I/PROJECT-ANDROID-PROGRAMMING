import { Inbox } from "lucide-react";
import styles from "./EmptyState.module.scss";

interface EmptyStateProps {
  title?: string;
  description?: string;
  action?: React.ReactNode;
}

export default function EmptyState({
  title = "Không có dữ liệu",
  description = "Chưa có mục nào để hiển thị.",
  action,
}: EmptyStateProps) {
  return (
    <div className={styles.empty}>
      <div className={styles.icon}>
        <Inbox size={24} />
      </div>
      <p className={styles.title}>{title}</p>
      <p className={styles.description}>{description}</p>
      {action}
    </div>
  );
}
