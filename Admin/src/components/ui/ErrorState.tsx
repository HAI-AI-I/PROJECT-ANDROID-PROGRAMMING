import { AlertCircle } from "lucide-react";
import Button from "./Button";
import styles from "./ErrorState.module.scss";

interface ErrorStateProps {
  title?: string;
  description?: string;
  onRetry?: () => void;
}

export default function ErrorState({
  title = "Đã xảy ra lỗi",
  description = "Không thể tải dữ liệu. Vui lòng thử lại.",
  onRetry,
}: ErrorStateProps) {
  return (
    <div className={styles.error}>
      <div className={styles.icon}>
        <AlertCircle size={24} />
      </div>
      <p className={styles.title}>{title}</p>
      <p className={styles.description}>{description}</p>
      {onRetry && (
        <Button variant="secondary" size="sm" onClick={onRetry}>
          Thử lại
        </Button>
      )}
    </div>
  );
}
