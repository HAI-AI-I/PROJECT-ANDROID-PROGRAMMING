import { BookOpen, BookMarked, Users, AlertTriangle } from "lucide-react";
import StatCard from "./StatCard";
import BorrowingChart from "./BorrowingChart";
import RecentBorrowings from "./RecentBorrowings";
import OverdueBooks from "./OverdueBooks";
import Card from "@/components/ui/Card";
import type {
  Borrowing,
  BorrowingChartData,
  DashboardStats,
  OverdueBook,
} from "@/types/Borrowing";
import styles from "./DashboardContent.module.scss";

interface DashboardContentProps {
  stats: DashboardStats;
  chartData: BorrowingChartData[];
  recentBorrowings: Borrowing[];
  overdueBooks: OverdueBook[];
}

export default function DashboardContent({
  stats,
  chartData,
  recentBorrowings,
  overdueBooks,
}: DashboardContentProps) {
  return (
    <>
      <div className={styles.welcome}>
        <h2 className={styles.greeting}>Xin chào, Admin</h2>
        <p className={styles.subtitle}>
          Tổng quan hệ thống thư viện hôm nay
        </p>
      </div>

      <div className={styles.stats}>
        <StatCard
          label="Tổng số sách"
          value={stats.totalBooks}
          icon={BookOpen}
          variant="primary"
        />
        <StatCard
          label="Đang được mượn"
          value={stats.currentlyBorrowed}
          icon={BookMarked}
          variant="info"
        />
        <StatCard
          label="Độc giả"
          value={stats.totalReaders}
          icon={Users}
          variant="success"
        />
        <StatCard
          label="Quá hạn"
          value={stats.overdueCount}
          icon={AlertTriangle}
          variant="danger"
        />
      </div>

      <div className={styles.grid}>
        <Card title="Lượt mượn sách">
          <BorrowingChart data={chartData} />
        </Card>
        <Card title="Sách quá hạn">
          <OverdueBooks data={overdueBooks} />
        </Card>
      </div>

      <div className={styles.bottom}>
        <Card title="Mượn sách gần đây">
          <RecentBorrowings data={recentBorrowings} />
        </Card>
      </div>
    </>
  );
}
