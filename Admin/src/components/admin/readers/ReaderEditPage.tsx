"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import ReaderForm from "./ReaderForm";
import LoadingState from "@/components/ui/LoadingState";
import EmptyState from "@/components/ui/EmptyState";
import Button from "@/components/ui/Button";
import { readerService } from "@/services/readerService";
import type { Reader } from "@/types/Reader";

export default function ReaderEditPage() {
  const params = useParams();
  const id = params.id as string;
  const [reader, setReader] = useState<Reader | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    readerService.getReaderById(id).then((d) => { setReader(d); setLoading(false); });
  }, [id]);

  if (loading) return <LoadingState />;
  if (!reader) return <EmptyState title="Không tìm thấy độc giả" action={<Link href="/admin/readers"><Button variant="secondary">Quay lại</Button></Link>} />;
  return <ReaderForm mode="edit" initialData={reader} />;
}
