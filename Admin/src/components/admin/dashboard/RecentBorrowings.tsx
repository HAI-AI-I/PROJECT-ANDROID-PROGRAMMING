"use client";

import Table from "@/components/ui/Table";
import { getBorrowingStatusBadge } from "@/components/ui/Badge";
import type { Borrowing } from "@/types/Borrowing";

interface RecentBorrowingsProps {
  data: Borrowing[];
}

export default function RecentBorrowings({ data }: RecentBorrowingsProps) {
  return (
    <Table
      columns={[
        { key: "id", header: "Mã" },
        { key: "readerName", header: "Độc giả" },
        { key: "bookTitle", header: "Sách" },
        { key: "borrowDate", header: "Ngày mượn" },
        { key: "dueDate", header: "Hạn trả" },
        {
          key: "status",
          header: "Trạng thái",
          render: (item) => getBorrowingStatusBadge(item.status),
        },
      ]}
      data={data}
      keyExtractor={(item) => item.id}
    />
  );
}
