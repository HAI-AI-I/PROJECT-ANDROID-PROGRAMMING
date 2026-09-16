"use client";

import { useCallback, useEffect, useState } from "react";
import Link from "next/link";
import Button from "@/components/ui/Button";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import { getSupportStatusBadge } from "@/components/ui/Badge";
import { supportService } from "@/services/supportService";
import type { SupportRequest } from "@/types/SupportRequest";
import pageStyles from "@/styles/page.module.scss";
import styles from "./SupportContent.module.scss";

export default function SupportListContent() {
  const [requests, setRequests] = useState<SupportRequest[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const fetchData = useCallback(async () => {
    setLoading(true); setError(false);
    try { setRequests(await supportService.getRequests()); }
    catch { setError(true); } finally { setLoading(false); }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Hỗ trợ</h2></div>
      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : requests.length === 0 ? (
          <EmptyState title="Không có yêu cầu hỗ trợ" />
        ) : (
          <div className={styles.list}>
            {requests.map((req) => (
              <div key={req.id} className={styles.card}>
                <div className={styles.info}>
                  <p className={styles.name}>{req.userName}</p>
                  <p className={styles.subject}>{req.subject}</p>
                  <p className={styles.date}>{req.createdDate}</p>
                </div>
                <div className={styles.cardActions}>
                  {getSupportStatusBadge(req.status)}
                  <Link href={`/admin/support/${req.id}`}><Button size="sm" variant="secondary">Xem chi tiết</Button></Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}
