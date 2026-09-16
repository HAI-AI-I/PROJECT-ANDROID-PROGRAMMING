"use client";

import { useCallback, useEffect, useState } from "react";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, LineChart, Line,
} from "recharts";
import Card from "@/components/ui/Card";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import { statisticsService } from "@/services/statisticsService";
import { statisticsData } from "@/data/statistics";
import pageStyles from "@/styles/page.module.scss";
import styles from "./StatisticsContent.module.scss";

type Stats = typeof statisticsData;

export default function StatisticsContent() {
  const [data, setData] = useState<Stats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true); setError(false);
    try { setData(await statisticsService.getStatistics()); }
    catch { setError(true); } finally { setLoading(false); }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  if (loading) return <LoadingState />;
  if (error || !data) return <ErrorState onRetry={fetchData} />;

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Thống kê</h2></div>

      <div className={styles.stats}>
        <div className={styles.statCard}><p className={styles.statLabel}>Tổng lượt mượn</p><p className={styles.statValue}>{data.totalBorrows.toLocaleString("vi-VN")}</p></div>
        <div className={styles.statCard}><p className={styles.statLabel}>Tổng lượt trả</p><p className={styles.statValue}>{data.totalReturns.toLocaleString("vi-VN")}</p></div>
        <div className={styles.statCard}><p className={styles.statLabel}>Sách quá hạn</p><p className={styles.statValue}>{data.overdueBooks}</p></div>
      </div>

      <div className={styles.grid}>
        <Card title="Xu hướng mượn sách">
          <div className={styles.chart}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={data.borrowTrend}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
                <XAxis dataKey="month" tick={{ fill: "var(--text-secondary)", fontSize: 13 }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fill: "var(--text-secondary)", fontSize: 13 }} axisLine={false} tickLine={false} />
                <Tooltip />
                <Line type="monotone" dataKey="count" stroke="var(--primary-light)" strokeWidth={2} dot={{ fill: "var(--primary)" }} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </Card>
        <Card title="Thống kê trả sách">
          <div className={styles.chart}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.returnTrend}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
                <XAxis dataKey="month" tick={{ fill: "var(--text-secondary)", fontSize: 13 }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fill: "var(--text-secondary)", fontSize: 13 }} axisLine={false} tickLine={false} />
                <Tooltip />
                <Bar dataKey="count" fill="var(--success)" radius={[6, 6, 0, 0]} maxBarSize={48} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Card>
      </div>

      <div className={styles.grid}>
        <Card title="Sách theo thể loại">
          <div className={styles.chart}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={data.booksByCategory} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" horizontal={false} />
                <XAxis type="number" tick={{ fill: "var(--text-secondary)", fontSize: 13 }} axisLine={false} tickLine={false} />
                <YAxis type="category" dataKey="category" tick={{ fill: "var(--text-secondary)", fontSize: 12 }} axisLine={false} tickLine={false} width={100} />
                <Tooltip />
                <Bar dataKey="count" fill="var(--primary-light)" radius={[0, 6, 6, 0]} maxBarSize={24} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </Card>
        <div className={styles.sideCol}>
          <Card title="Sách phổ biến">
            <div className={styles.list}>
              {data.popularBooks.map((b, i) => (
                <div key={b.title} className={styles.listItem}>
                  <span><span className={styles.listRank}>#{i + 1}</span>{b.title}</span>
                  <span style={{ color: "var(--text-secondary)" }}>{b.count} lượt</span>
                </div>
              ))}
            </div>
          </Card>
          <Card title="Độc giả tích cực">
            <div className={styles.list}>
              {data.activeReaders.map((r, i) => (
                <div key={r.name} className={styles.listItem}>
                  <span><span className={styles.listRank}>#{i + 1}</span>{r.name}</span>
                  <span style={{ color: "var(--text-secondary)" }}>{r.count} lượt</span>
                </div>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </>
  );
}
