import { ChevronLeft, ChevronRight } from "lucide-react";
import styles from "./Pagination.module.scss";

interface PaginationProps {
  page: number;
  totalPages: number;
  total: number;
  pageSize: number;
  onPageChange: (page: number) => void;
}

export default function Pagination({
  page,
  totalPages,
  total,
  pageSize,
  onPageChange,
}: PaginationProps) {
  const start = total === 0 ? 0 : (page - 1) * pageSize + 1;
  const end = Math.min(page * pageSize, total);

  const pages: number[] = [];
  for (let i = 1; i <= totalPages; i++) {
    if (
      i === 1 ||
      i === totalPages ||
      (i >= page - 1 && i <= page + 1)
    ) {
      pages.push(i);
    }
  }

  return (
    <div className={styles.pagination}>
      <span className={styles.info}>
        Hiển thị {start}–{end} / {total} kết quả
      </span>
      <div className={styles.controls}>
        <button
          className={styles.pageBtn}
          disabled={page <= 1}
          onClick={() => onPageChange(page - 1)}
          type="button"
          aria-label="Trang trước"
        >
          <ChevronLeft size={16} />
        </button>
        {pages.map((p, i) => {
          const prev = pages[i - 1];
          const showEllipsis = prev !== undefined && p - prev > 1;
          return (
            <span key={p} style={{ display: "contents" }}>
              {showEllipsis && (
                <span className={styles.pageBtn} style={{ cursor: "default" }}>
                  …
                </span>
              )}
              <button
                className={`${styles.pageBtn} ${p === page ? styles.active : ""}`}
                onClick={() => onPageChange(p)}
                type="button"
              >
                {p}
              </button>
            </span>
          );
        })}
        <button
          className={styles.pageBtn}
          disabled={page >= totalPages}
          onClick={() => onPageChange(page + 1)}
          type="button"
          aria-label="Trang sau"
        >
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
}
