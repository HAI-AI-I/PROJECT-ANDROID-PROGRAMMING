"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Input from "@/components/ui/Input";
import Select from "@/components/ui/Select";
import Button from "@/components/ui/Button";
import { useToast } from "@/components/ui/Toast";
import { bookCategories } from "@/data/books";
import { bookService } from "@/services/bookService";
import type { Book, BookFormData } from "@/types/Book";
import styles from "./BookForm.module.scss";

const readFileAsDataUrl = (file: File): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result ?? ""));
    reader.onerror = () => reject(new Error("Không đọc được file ảnh"));
    reader.readAsDataURL(file);
  });

interface BookFormProps {
  initialData?: Book;
  mode: "create" | "edit";
}

const emptyForm: BookFormData = {
  title: "",
  author: "",
  category: "",
  publisher: "",
  publishYear: "",
  quantity: "",
};

function validate(form: BookFormData): Partial<Record<keyof BookFormData, string>> {
  const errors: Partial<Record<keyof BookFormData, string>> = {};
  if (!form.title.trim()) errors.title = "Vui lòng nhập tên sách";
  if (!form.author.trim()) errors.author = "Vui lòng nhập tác giả";
  if (!form.category) errors.category = "Vui lòng chọn thể loại";
  if (!form.publisher.trim()) errors.publisher = "Vui lòng nhập nhà xuất bản";
  if (!form.publishYear || isNaN(Number(form.publishYear))) {
    errors.publishYear = "Năm xuất bản không hợp lệ";
  } else if (Number(form.publishYear) < 1000 || Number(form.publishYear) > 2100) {
    errors.publishYear = "Năm xuất bản không hợp lệ";
  }
  if (!form.quantity || isNaN(Number(form.quantity)) || Number(form.quantity) < 0) {
    errors.quantity = "Số lượng phải >= 0";
  }
  return errors;
}

export default function BookForm({ initialData, mode }: BookFormProps) {
  const router = useRouter();
  const { showToast } = useToast();
  const [form, setForm] = useState<BookFormData>(
    initialData
      ? {
          title: initialData.title,
          author: initialData.author,
          category: initialData.category,
          publisher: initialData.publisher,
          publishYear: String(initialData.publishYear),
          quantity: String(initialData.quantity),
          cover: initialData.cover,
        }
      : emptyForm
  );
  const [errors, setErrors] = useState<Partial<Record<keyof BookFormData, string>>>({});
  const [loading, setLoading] = useState(false);

  const update = (field: keyof BookFormData, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: undefined }));
  };

  const handleCoverChange = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    try {
      const url = await readFileAsDataUrl(file);
      update("cover", url);
    } catch {
      showToast("Không thể đọc ảnh đã chọn", "error");
    } finally {
      event.target.value = "";
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const validationErrors = validate(form);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setLoading(true);
    try {
      if (mode === "create") {
        const book = await bookService.createBook(form);
        showToast("Thêm sách thành công");
        router.push(`/admin/books/${book.bookId}`);
      } else if (initialData) {
        await bookService.updateBook(initialData.bookId, form);
        showToast("Cập nhật sách thành công");
        router.push(`/admin/books/${initialData.bookId}`);
      }
    } catch {
      showToast("Có lỗi xảy ra, vui lòng thử lại", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className={styles.form} onSubmit={handleSubmit}>
      <div className={styles.section}>
        <h2 className={styles.sectionTitle}>Thông tin sách</h2>

        <div className={styles.grid}>
          <div className={styles.full}>
            <p className={styles.sectionTitle} style={{ fontSize: 14, marginBottom: 8 }}>
              Ảnh bìa
            </p>

            <label className={styles.coverUploadLabel}>
              <div className={styles.coverPreview}>
                {form.cover ? (
                  <img src={form.cover} alt="Bìa sách" />
                ) : (
                  <span>Chưa có ảnh</span>
                )}
                <span className={styles.coverOverlay}>{form.cover ? "Thay ảnh" : "Chọn ảnh"}</span>
              </div>
              <input type="file" accept="image/*" onChange={handleCoverChange} />
            </label>

            {form.cover && (
              <div className={styles.coverRemoveRow}>
                <button type="button" className={styles.removeCoverButton} onClick={() => update("cover", "")}>
                  Xóa ảnh
                </button>
              </div>
            )}
          </div>

          <div className={styles.full}>
            <Input
              label="Tên sách"
              value={form.title}
              onChange={(e) => update("title", e.target.value)}
              error={errors.title}
              placeholder="Nhập tên sách"
            />
          </div>

          <div className={styles.full}>
            <Input
              label="URL ảnh bìa"
              value={form.cover ?? ""}
              onChange={(e) => update("cover", e.target.value)}
              placeholder="https://example.com/cover.jpg"
            />
          </div>

          <Input
            label="Tác giả"
            value={form.author}
            onChange={(e) => update("author", e.target.value)}
            error={errors.author}
            placeholder="Nhập tác giả"
          />

          <Select
            label="Thể loại"
            value={form.category}
            onChange={(e) => update("category", e.target.value)}
            placeholder="Chọn thể loại"
            options={bookCategories.map((c) => ({ value: c, label: c }))}
          />
          {errors.category && (
            <span style={{ color: "var(--danger)", fontSize: 12, gridColumn: "1 / -1", marginTop: -8 }}>
              {errors.category}
            </span>
          )}

          <Input
            label="Nhà xuất bản"
            value={form.publisher}
            onChange={(e) => update("publisher", e.target.value)}
            error={errors.publisher}
            placeholder="Nhập nhà xuất bản"
          />

          <Input
            label="Năm xuất bản"
            type="number"
            value={form.publishYear}
            onChange={(e) => update("publishYear", e.target.value)}
            error={errors.publishYear}
            placeholder="2024"
          />

          <Input
            label="Số lượng"
            type="number"
            value={form.quantity}
            onChange={(e) => update("quantity", e.target.value)}
            error={errors.quantity}
            placeholder="0"
            min={0}
          />
        </div>

        <div className={styles.actions}>
          <Button
            type="button"
            variant="secondary"
            onClick={() => router.back()}
          >
            Hủy
          </Button>
          <Button type="submit" disabled={loading}>
            {loading ? "Đang lưu..." : "Lưu sách"}
          </Button>
        </div>
      </div>
    </form>
  );
}
