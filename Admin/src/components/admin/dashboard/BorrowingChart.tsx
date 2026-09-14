"use client";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import type { BorrowingChartData } from "@/types/Borrowing";
import styles from "./BorrowingChart.module.scss";

interface BorrowingChartProps {
  data: BorrowingChartData[];
}

export default function BorrowingChart({ data }: BorrowingChartProps) {
  return (
    <div className={styles.chart}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data} margin={{ top: 8, right: 8, left: -16, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" vertical={false} />
          <XAxis
            dataKey="day"
            tick={{ fill: "var(--text-secondary)", fontSize: 13 }}
            axisLine={{ stroke: "var(--border)" }}
            tickLine={false}
          />
          <YAxis
            tick={{ fill: "var(--text-secondary)", fontSize: 13 }}
            axisLine={false}
            tickLine={false}
          />
          <Tooltip
            content={({ active, payload, label }) => {
              if (!active || !payload?.length) return null;
              return (
                <div className={styles.tooltip}>
                  <div className={styles.tooltipLabel}>{label}</div>
                  <div className={styles.tooltipValue}>
                    {payload[0].value} lượt mượn
                  </div>
                </div>
              );
            }}
          />
          <Bar
            dataKey="count"
            fill="var(--primary-light)"
            radius={[6, 6, 0, 0]}
            maxBarSize={48}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
