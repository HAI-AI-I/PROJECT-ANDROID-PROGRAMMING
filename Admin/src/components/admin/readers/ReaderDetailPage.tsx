"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { Pencil } from "lucide-react";
import Button from "@/components/ui/Button";
import LoadingState from "@/components/ui/LoadingState";
import EmptyState from "@/components/ui/EmptyState";
import { getReaderStatusBadge } from "@/components/ui/Badge";
import { readerService } from "@/services/readerService";
import type { Reader } from "@/types/Reader";
import styles from "../books/BookDetail.module.scss";

export default function ReaderDetailPage() {
  const params = useParams();
  const id = params.id as string;
  const [reader, setReader] = useState<Reader | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    readerService.getReaderById(id).then((d) => { setReader(d); setLoading(false); });
  }, [id]);

  if (loading) return <LoadingState />;
  if (!reader) return <EmptyState title="Không tìm thấy độc giả" action={<Link href="/admin/readers"><Button variant="secondary">Quay lại</Button></Link>} />;

  const initials = reader.name.split(" ").slice(-2).map((w) => w[0]).join("").toUpperCase();

  return (
    <div className={styles.wrapper}>
      <div className={styles.top}>
        <div className={styles.cover} style={{ fontSize: 32, fontWeight: 700, color: "var(--primary)" }}>{initials}</div>
        <div className={styles.info}>
          <h2 className={styles.bookTitle}>{reader.name}</h2>
          {getReaderStatusBadge(reader.status)}
          <div className={styles.meta}>
            <div className={styles.metaItem}><span className={styles.metaLabel}>Mã độc giả</span><span className={styles.metaValue}>{reader.readerId}</span></div>
            <div className={styles.metaItem}><span className={styles.metaLabel}>Email</span><span className={styles.metaValue}>{reader.email}</span></div>
            <div className={styles.metaItem}><span className={styles.metaLabel}>Số điện thoại</span><span className={styles.metaValue}>{reader.phone}</span></div>
            <div className={styles.metaItem}><span className={styles.metaLabel}>Sách đang mượn</span><span className={styles.metaValue}>{reader.booksBorrowing}</span></div>
            <div className={styles.metaItem}><span className={styles.metaLabel}>Ngày đăng ký</span><span className={styles.metaValue}>{reader.registeredDate}</span></div>
          </div>
          <div className={styles.actions}>
            <Link href={`/admin/readers/${reader.readerId}/edit`}><Button variant="secondary"><Pencil size={16} />Sửa</Button></Link>
            <Link href="/admin/readers"><Button variant="ghost">Quay lại</Button></Link>
          </div>
        </div>
      </div>
    </div>
  );
}
