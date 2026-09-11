"use client";

import Link from "next/link";
import { Eye, Pencil, Trash2 } from "lucide-react";
import Table from "@/components/ui/Table";
import Button from "@/components/ui/Button";
import { getBookStatusBadge } from "@/components/ui/Badge";
import type { Book } from "@/types/Book";
import { getBookStatus } from "@/types/Book";
import styles from "./BookTable.module.scss";

interface BookTableProps {
  books: Book[];
  onDelete: (book: Book) => void;
  onSelectionChange?: (keys: string[]) => void;
}

export default function BookTable({ books, onDelete, onSelectionChange }: BookTableProps) {
  return (
    <Table
      columns={[
        {
          key: "title",
          header: "Sách",
          render: (item) => (
            <span className={styles.titleCell}>{item.title}</span>
          ),
        },
        { key: "author", header: "Tác giả" },
        { key: "category", header: "Thể loại" },
        { key: "quantity", header: "Số lượng" },
        { key: "availableQuantity", header: "Còn lại" },
        {
          key: "status",
          header: "Trạng thái",
          render: (item) => getBookStatusBadge(getBookStatus(item)),
        },
        {
          key: "actions",
          header: "Thao tác",
          sortable: false,
          render: (item) => (
            <div className={styles.actions}>
              <Link href={`/admin/books/${item.bookId}`}>
                <Button variant="ghost" size="icon" title="Xem chi tiết">
                  <Eye size={16} />
                </Button>
              </Link>
              <Link href={`/admin/books/${item.bookId}/edit`}>
                <Button variant="ghost" size="icon" title="Sửa">
                  <Pencil size={16} />
                </Button>
              </Link>
              <Button
                variant="ghost"
                size="icon"
                title="Xóa"
                onClick={() => onDelete(item)}
              >
                <Trash2 size={16} />
              </Button>
            </div>
          ),
        },
      ]}
      data={books}
      keyExtractor={(item) => item.bookId}
      selectable
      onSelectionChange={onSelectionChange}
    />
  );
}
