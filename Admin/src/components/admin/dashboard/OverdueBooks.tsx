import Badge from "@/components/ui/Badge";
import type { OverdueBook } from "@/types/Borrowing";
import styles from "./OverdueBooks.module.scss";

interface OverdueBooksProps {
  data: OverdueBook[];
}

export default function OverdueBooks({ data }: OverdueBooksProps) {
  return (
    <div className={styles.list}>
      {data.map((item) => (
        <div key={item.id} className={styles.item}>
          <div className={styles.info}>
            <p className={styles.title}>{item.bookTitle}</p>
            <p className={styles.meta}>
              {item.readerName} · Hạn trả: {item.dueDate}
            </p>
          </div>
          <div className={styles.right}>
            <Badge variant="danger">Quá hạn</Badge>
            <span className={styles.days}>{item.overdueDays} ngày</span>
          </div>
        </div>
      ))}
    </div>
  );
}
