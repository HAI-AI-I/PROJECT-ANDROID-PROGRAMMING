"use client";

import { useCallback, useEffect, useState } from "react";
import { Search } from "lucide-react";
import Select from "@/components/ui/Select";
import Pagination from "@/components/ui/Pagination";
import EmptyState from "@/components/ui/EmptyState";
import LoadingState from "@/components/ui/LoadingState";
import ErrorState from "@/components/ui/ErrorState";
import Table from "@/components/ui/Table";
import { getUserRoleBadge, getReaderStatusBadge } from "@/components/ui/Badge";
import { userService } from "@/services/userService";
import type { User } from "@/types/User";
import pageStyles from "@/styles/page.module.scss";

const PAGE_SIZE = 8;

export default function UsersListContent() {
  const [users, setUsers] = useState<User[]>([]);
  const [total, setTotal] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [search, setSearch] = useState("");
  const [role, setRole] = useState("");
  const [status, setStatus] = useState("");

  const fetchData = useCallback(async () => {
    setLoading(true); setError(false);
    try {
      const r = await userService.getUsers({ search, role: role || undefined, status: status || undefined, page, pageSize: PAGE_SIZE });
      setUsers(r.items); setTotal(r.total); setTotalPages(r.totalPages);
    } catch { setError(true); } finally { setLoading(false); }
  }, [search, role, status, page]);

  useEffect(() => { fetchData(); }, [fetchData]);

  return (
    <>
      <div className={pageStyles.pageHeader}><h2 className={pageStyles.pageTitle}>Người dùng</h2></div>
      <div className={pageStyles.toolbar}>
        <div className={pageStyles.searchWrap}>
          <Search size={16} color="var(--text-tertiary)" />
          <input placeholder="Tìm tên, email..." value={search} onChange={(e) => { setSearch(e.target.value); setPage(1); }} />
        </div>
        <div className={pageStyles.filters}>
          <Select className={pageStyles.filterSelect} value={role} onChange={(e) => { setRole(e.target.value); setPage(1); }} placeholder="Vai trò"
            options={[{ value: "admin", label: "Admin" }, { value: "librarian", label: "Librarian" }, { value: "reader", label: "Reader" }]} />
          <Select className={pageStyles.filterSelect} value={status} onChange={(e) => { setStatus(e.target.value); setPage(1); }} placeholder="Trạng thái"
            options={[{ value: "active", label: "Hoạt động" }, { value: "inactive", label: "Không hoạt động" }]} />
        </div>
      </div>
      <div className={pageStyles.card}>
        {loading ? <LoadingState /> : error ? <ErrorState onRetry={fetchData} /> : users.length === 0 ? (
          <EmptyState title="Không tìm thấy người dùng" />
        ) : (
          <>
            <Table columns={[
              { key: "avatar", header: "Avatar", render: (u) => (
                <div style={{ width: 32, height: 32, borderRadius: 8, background: "var(--primary-light)", color: "white", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12, fontWeight: 600 }}>
                  {u.name.charAt(0).toUpperCase()}
                </div>
              )},
              { key: "name", header: "Tên", render: (u) => <span style={{ fontWeight: 500 }}>{u.name}</span> },
              { key: "email", header: "Email" },
              { key: "role", header: "Vai trò", render: (u) => getUserRoleBadge(u.role) },
              { key: "status", header: "Trạng thái", render: (u) => getReaderStatusBadge(u.status) },
              { key: "createdDate", header: "Ngày tạo" },
            ]} data={users} keyExtractor={(u) => u.userId} />
            <Pagination page={page} totalPages={totalPages} total={total} pageSize={PAGE_SIZE} onPageChange={setPage} />
          </>
        )}
      </div>
    </>
  );
}
