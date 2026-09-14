"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Input from "@/components/ui/Input";
import Select from "@/components/ui/Select";
import Button from "@/components/ui/Button";
import { useToast } from "@/components/ui/Toast";
import { readerService } from "@/services/readerService";
import type { Reader, ReaderFormData, ReaderStatus } from "@/types/Reader";
import styles from "../books/BookForm.module.scss";

interface ReaderFormProps {
  initialData?: Reader;
  mode: "create" | "edit";
}

export default function ReaderForm({ initialData, mode }: ReaderFormProps) {
  const router = useRouter();
  const { showToast } = useToast();
  const [form, setForm] = useState<ReaderFormData>({
    name: initialData?.name ?? "",
    email: initialData?.email ?? "",
    phone: initialData?.phone ?? "",
    status: initialData?.status ?? "active",
  });
  const [errors, setErrors] = useState<Partial<Record<keyof ReaderFormData, string>>>({});
  const [loading, setLoading] = useState(false);

  const update = (field: keyof ReaderFormData, value: string) => {
    setForm((p) => ({ ...p, [field]: value }));
    setErrors((p) => ({ ...p, [field]: undefined }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const errs: typeof errors = {};
    if (!form.name.trim()) errs.name = "Vui lòng nhập họ tên";
    if (!form.email.trim()) errs.email = "Vui lòng nhập email";
    if (!form.phone.trim()) errs.phone = "Vui lòng nhập SĐT";
    if (Object.keys(errs).length) { setErrors(errs); return; }

    setLoading(true);
    try {
      if (mode === "create") {
        const r = await readerService.createReader(form);
        showToast("Thêm độc giả thành công");
        router.push(`/admin/readers/${r.readerId}`);
      } else if (initialData) {
        await readerService.updateReader(initialData.readerId, form);
        showToast("Cập nhật độc giả thành công");
        router.push(`/admin/readers/${initialData.readerId}`);
      }
    } catch {
      showToast("Có lỗi xảy ra", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className={styles.form} onSubmit={handleSubmit}>
      <div className={styles.section}>
        <h2 className={styles.sectionTitle}>{mode === "create" ? "Thêm độc giả" : "Sửa độc giả"}</h2>
        <div className={styles.grid}>
          <div className={styles.full}><Input label="Họ tên" value={form.name} onChange={(e) => update("name", e.target.value)} error={errors.name} /></div>
          <Input label="Email" type="email" value={form.email} onChange={(e) => update("email", e.target.value)} error={errors.email} />
          <Input label="Số điện thoại" value={form.phone} onChange={(e) => update("phone", e.target.value)} error={errors.phone} />
          <Select label="Trạng thái" value={form.status} onChange={(e) => update("status", e.target.value as ReaderStatus)}
            options={[
              { value: "active", label: "Hoạt động" }, { value: "inactive", label: "Không hoạt động" }, { value: "suspended", label: "Tạm khóa" },
            ]} />
        </div>
        <div className={styles.actions}>
          <Button type="button" variant="secondary" onClick={() => router.back()}>Hủy</Button>
          <Button type="submit" disabled={loading}>{loading ? "Đang lưu..." : "Lưu"}</Button>
        </div>
      </div>
    </form>
  );
}
