import styles from "./Badge.module.scss";

type BadgeVariant =
  | "default"
  | "success"
  | "warning"
  | "danger"
  | "info"
  | "primary";

interface BadgeProps {
  children: React.ReactNode;
  variant?: BadgeVariant;
  className?: string;
}

export default function Badge({
  children,
  variant = "default",
  className = "",
}: BadgeProps) {
  return (
    <span className={`${styles.badge} ${styles[variant]} ${className}`}>
      {children}
    </span>
  );
}

export function getBookStatusBadge(status: string) {
  const map: Record<string, { label: string; variant: BadgeVariant }> = {
    available: { label: "Có sẵn", variant: "success" },
    borrowed: { label: "Đang được mượn", variant: "info" },
    out_of_stock: { label: "Hết sách", variant: "danger" },
  };
  const config = map[status] ?? { label: status, variant: "default" as const };
  return <Badge variant={config.variant}>{config.label}</Badge>;
}

export function getReaderStatusBadge(status: string) {
  const map: Record<string, { label: string; variant: BadgeVariant }> = {
    active: { label: "Hoạt động", variant: "success" },
    inactive: { label: "Không hoạt động", variant: "default" },
    suspended: { label: "Tạm khóa", variant: "danger" },
  };
  const config = map[status] ?? { label: status, variant: "default" as const };
  return <Badge variant={config.variant}>{config.label}</Badge>;
}

export function getUserRoleBadge(role: string) {
  const map: Record<string, { label: string; variant: BadgeVariant }> = {
    admin: { label: "ADMIN", variant: "primary" },
    librarian: { label: "LIBRARIAN", variant: "info" },
    reader: { label: "READER", variant: "default" },
  };
  const config = map[role] ?? { label: role.toUpperCase(), variant: "default" as const };
  return <Badge variant={config.variant}>{config.label}</Badge>;
}

export function getHistoryActionBadge(action: string) {
  const map: Record<string, { label: string; variant: BadgeVariant }> = {
    borrow: { label: "Mượn", variant: "info" },
    return: { label: "Trả", variant: "success" },
    extend: { label: "Gia hạn", variant: "primary" },
    fine: { label: "Phạt", variant: "warning" },
    cancel_request: { label: "Hủy yêu cầu", variant: "danger" },
  };
  const config = map[action] ?? { label: action, variant: "default" as const };
  return <Badge variant={config.variant}>{config.label}</Badge>;
}

export function getSupportStatusBadge(status: string) {
  const map: Record<string, { label: string; variant: BadgeVariant }> = {
    open: { label: "Mở", variant: "warning" },
    in_progress: { label: "Đang xử lý", variant: "info" },
    resolved: { label: "Đã xử lý", variant: "success" },
    closed: { label: "Đóng", variant: "default" },
  };
  const config = map[status] ?? { label: status, variant: "default" as const };
  return <Badge variant={config.variant}>{config.label}</Badge>;
}

export function getBorrowingStatusBadge(status: string) {
  const map: Record<string, { label: string; variant: BadgeVariant }> = {
    borrowing: { label: "Đang mượn", variant: "info" },
    returned: { label: "Đã trả", variant: "success" },
    overdue: { label: "Quá hạn", variant: "danger" },
    pending: { label: "Chờ duyệt", variant: "warning" },
  };
  const config = map[status] ?? { label: status, variant: "default" as const };
  return <Badge variant={config.variant}>{config.label}</Badge>;
}
