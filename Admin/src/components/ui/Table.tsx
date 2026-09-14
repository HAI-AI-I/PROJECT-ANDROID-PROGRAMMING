"use client";

import { useMemo, useState } from "react";
import { ArrowDown, ArrowUp, ChevronsUpDown } from "lucide-react";
import styles from "./Table.module.scss";

interface Column<T> {
  key: string;
  header: string;
  render?: (item: T) => React.ReactNode;
  sortable?: boolean;
}

interface TableProps<T> {
  columns: Column<T>[];
  data: T[];
  keyExtractor: (item: T) => string;
  selectable?: boolean;
  onSelectionChange?: (keys: string[]) => void;
}

export default function Table<T>({
  columns,
  data,
  keyExtractor,
  selectable = false,
  onSelectionChange,
}: TableProps<T>) {
  const [sort, setSort] = useState<{ key: string; direction: "asc" | "desc" } | null>(null);
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  const sortedData = useMemo(() => {
    if (!sort) return data;
    return [...data].sort((left, right) => {
      const leftValue = String((left as Record<string, unknown>)[sort.key] ?? "");
      const rightValue = String((right as Record<string, unknown>)[sort.key] ?? "");
      const comparison = leftValue.localeCompare(rightValue, "vi", { numeric: true });
      return sort.direction === "asc" ? comparison : -comparison;
    });
  }, [data, sort]);

  const updateSelection = (keys: string[]) => {
    setSelectedKeys(keys);
    onSelectionChange?.(keys);
  };

  const allSelected = data.length > 0 && data.every((item) => selectedKeys.includes(keyExtractor(item)));

  const toggleSort = (key: string) => {
    setSort((current) => current?.key === key
      ? { key, direction: current.direction === "asc" ? "desc" : "asc" }
      : { key, direction: "asc" });
  };

  return (
    <div className={styles.wrapper}>
      <table className={styles.table}>
        <thead>
          <tr>
            {selectable && (
              <th className={styles.checkboxCell}>
                <input
                  type="checkbox"
                  aria-label="Chọn tất cả"
                  checked={allSelected}
                  onChange={(event) => updateSelection(event.target.checked ? data.map(keyExtractor) : [])}
                />
              </th>
            )}
            {columns.map((col) => (
              <th key={col.key}>
                {col.sortable === false ? col.header : (
                  <button className={styles.sortButton} type="button" onClick={() => toggleSort(col.key)}>
                    {col.header}
                    {sort?.key === col.key
                      ? sort.direction === "asc" ? <ArrowUp size={14} /> : <ArrowDown size={14} />
                      : <ChevronsUpDown size={14} />}
                  </button>
                )}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {sortedData.map((item) => (
            <tr key={keyExtractor(item)}>
              {selectable && (
                <td className={styles.checkboxCell} data-label="Chọn">
                  <input
                    type="checkbox"
                    aria-label={`Chọn ${keyExtractor(item)}`}
                    checked={selectedKeys.includes(keyExtractor(item))}
                    onChange={(event) => updateSelection(event.target.checked
                      ? [...selectedKeys, keyExtractor(item)]
                      : selectedKeys.filter((key) => key !== keyExtractor(item)))}
                  />
                </td>
              )}
              {columns.map((col) => (
                <td key={col.key} data-label={col.header}>
                  {col.render
                    ? col.render(item)
                    : String((item as Record<string, unknown>)[col.key] ?? "")}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
