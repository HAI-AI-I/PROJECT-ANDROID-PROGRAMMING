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
    password: "",
  });
  const [errors, setErrors] = useState<Partial<Record<keyof ReaderFormData, string>>>({});
  const [loading, setLoading] = useState(false);

  const update = (field: keyof ReaderFormData, value: string) => {
    setForm((current) => ({ ...current, [field]: value }));
    setErrors((current) => ({ ...current, [field]: undefined }));
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    const nextErrors: typeof errors = {};
    if (!form.name.trim()) nextErrors.name = "Vui lòng nhập họ tên";
    if (!form.email.trim()) nextErrors.email = "Vui lòng nhập email";
    if (!/^\d{10,15}$/.test(form.phone.trim())) nextErrors.phone = "Số điện thoại phải có từ 10 đến 15 chữ số";
    if (mode === "create" && (!form.password || form.password.length < 6)) {
      nextErrors.password = "Mật khẩu phải có ít nhất 6 ký tự";
    }
    if (Object.keys(nextErrors).length) {
      setErrors(nextErrors);
      return;
    }

    setLoading(true);
    try {
      if (mode === "create") {
        const reader = await readerService.createReader(form);
        showToast("Thêm độc giả thành công");
        router.push(`/admin/readers/${reader.readerId}`);
      } else if (initialData) {
        await readerService.updateReader(initialData.readerId, form);
        showToast("Cập nhật độc giả thành công");
        router.push(`/admin/readers/${initialData.readerId}`);
      }
    } catch (error) {
      showToast(error instanceof Error ? error.message : "Có lỗi xảy ra", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className={styles.form} onSubmit={handleSubmit}>
      <div className={styles.section}>
        <h2 className={styles.sectionTitle}>{mode === "create" ? "Thêm độc giả" : "Sửa độc giả"}</h2>
        <div className={styles.grid}>
          <div className={styles.full}>
            <Input label="Họ tên" value={form.name} onChange={(event) => update("name", event.target.value)} error={errors.name} />
          </div>
          <Input label="Email" type="email" value={form.email} onChange={(event) => update("email", event.target.value)} error={errors.email} />
          <Input label="Số điện thoại" inputMode="numeric" value={form.phone} onChange={(event) => update("phone", event.target.value)} error={errors.phone} />
          {mode === "create" && (
            <Input label="Mật khẩu ban đầu" type="password" value={form.password} onChange={(event) => update("password", event.target.value)} error={errors.password} />
          )}
          <Select label="Trạng thái" value={form.status} onChange={(event) => update("status", event.target.value as ReaderStatus)} options={[
            { value: "active", label: "Hoạt động" },
            { value: "inactive", label: "Đã khóa" },
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
