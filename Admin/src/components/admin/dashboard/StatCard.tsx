import type { LucideIcon } from "lucide-react";
import styles from "./StatCard.module.scss";

interface StatCardProps {
  label: string;
  value: number;
  icon: LucideIcon;
  variant?: "primary" | "info" | "success" | "danger";
}

export default function StatCard({
  label,
  value,
  icon: Icon,
  variant = "primary",
}: StatCardProps) {
  return (
    <div className={`${styles.card} ${styles[variant]}`}>
      <div className={styles.iconWrap}>
        <Icon size={22} />
      </div>
      <div className={styles.info2}>
        <p className={styles.label}>{label}</p>
        <p className={styles.value}>{value.toLocaleString("vi-VN")}</p>
      </div>
    </div>
  );
}
