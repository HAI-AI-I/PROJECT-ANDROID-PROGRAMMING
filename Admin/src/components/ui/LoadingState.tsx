import styles from "./LoadingState.module.scss";

interface LoadingStateProps {
  message?: string;
}

export default function LoadingState({ message = "Đang tải..." }: LoadingStateProps) {
  return (
    <div className={styles.loading}>
      <div className={styles.spinner} />
      <span>{message}</span>
    </div>
  );
}
