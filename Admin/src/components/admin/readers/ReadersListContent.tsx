"use client";

import { useCallback, useEffect, useState } from "react";
import Link from "next/link";
import { Plus, Search, Eye, Pencil, Trash2 } from "lucide-react";
import Button from "@/components/ui/Button";
import Select from "@/components/ui/Select";
import Pagination from "@/components/ui/Pagination";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import Modal from "@/components/ui/Modal";
import Table from "@/components/ui/Table";
import { getReaderStatusBadge } from "@/components/ui/Badge";
import { useToast } from "@/components/ui/Toast";
import { readerService } from "@/services/readerService";
import type { Reader } from "@/types/Reader";
import pageStyles from "@/styles/page.module.scss";

const PAGE_SIZE = 8;

function Avatar({ name }: { name: string }) {
  const initials = name.split(" ").slice(-2).map((w) => w[0]).join("").toUpperCase();
  return (
    <div style={{
      width: 32, height: 32, borderRadius: 8, background: "var(--primary)",
      color: "white", display: "flex", alignItems: "center", justifyContent: "center",
      fontSize: 12, fontWeight: 600,
    }}>{initials}</div>
  );
}

export default function ReadersListContent() {
  const { showToast } = useToast();
  const [readers, setReaders] = useState<Reader[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("");
  const [deleteTarget, setDeleteTarget] = useState<Reader | null>(null);

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      const result = await readerService.getReaders({ search, status: status || undefined, page, pageSize: PAGE_SIZE });
      setReaders(result.items);
      setTotal(result.total);
      setTotalPages(result.totalPages);
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, [search, status, page]);

  useEffect(() => { fetchData(); }, [fetchData]);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    const ok = await readerService.deleteReader(deleteTarget.readerId);
    if (ok) { showToast("Xóa độc giả thành công"); setDeleteTarget(null); fetchData(); }
    else showToast("Không thể xóa độc giả", "error");
  };

  return (
    <>
      <div className={pageStyles.pageHeader}>
        <h2 className={pageStyles.pageTitle}>Quản lý độc giả</h2>
        <Link href="/admin/readers/new"><Button><Plus size={16} />Thêm độc giả</Button></Link>
      </div>
      <div className={pageStyles.toolbar}>
        <div className={pageStyles.searchWrap}>
          <Search size={16} color="var(--text-tertiary)" />
          <input placeholder="Tìm kiếm độc giả..." value={search} onChange={(e) => { setSearch(e.target.value); setPage(1); }} />
        </div>
        <Select className={pageStyles.filterSelect} value={status} onChange={(e) => { setStatus(e.target.value); setPage(1); }}
          placeholder="Trạng thái" options={[
            { value: "active", label: "Hoạt động" }, { value: "inactive", label: "Không hoạt động" }, { value: "suspended", label: "Tạm khóa" },
          ]} />
      </div>
      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : readers.length === 0 ? (
          <EmptyState title="Không tìm thấy độc giả" action={<Link href="/admin/readers/new"><Button size="sm">Thêm độc giả</Button></Link>} />
        ) : (
          <>
            <Table columns={[
              { key: "readerId", header: "Mã" },
              { key: "avatar", header: "Avatar", render: (r) => <Avatar name={r.name} /> },
              { key: "name", header: "Họ tên", render: (r) => <span style={{ fontWeight: 500 }}>{r.name}</span> },
              { key: "email", header: "Email" },
              { key: "phone", header: "SĐT" },
              { key: "booksBorrowing", header: "Đang mượn" },
              { key: "status", header: "Trạng thái", render: (r) => getReaderStatusBadge(r.status) },
              { key: "registeredDate", header: "Ngày đăng ký" },
              { key: "actions", header: "Thao tác", sortable: false, render: (r) => (
                <div style={{ display: "flex", gap: 4 }}>
                  <Link href={`/admin/readers/${r.readerId}`}><Button variant="ghost" size="icon"><Eye size={16} /></Button></Link>
                  <Link href={`/admin/readers/${r.readerId}/edit`}><Button variant="ghost" size="icon"><Pencil size={16} /></Button></Link>
                  <Button variant="ghost" size="icon" onClick={() => setDeleteTarget(r)}><Trash2 size={16} /></Button>
                </div>
              )},
            ]} data={readers} keyExtractor={(r) => r.readerId} />
            <Pagination page={page} totalPages={totalPages} total={total} pageSize={PAGE_SIZE} onPageChange={setPage} />
          </>
        )}
      </div>
      <Modal open={!!deleteTarget} title="Xóa độc giả" confirmLabel="Xóa" variant="danger" onConfirm={handleDelete} onCancel={() => setDeleteTarget(null)}>
        Bạn có chắc muốn xóa độc giả <strong>{deleteTarget?.name}</strong>?
      </Modal>
    </>
  );
}
