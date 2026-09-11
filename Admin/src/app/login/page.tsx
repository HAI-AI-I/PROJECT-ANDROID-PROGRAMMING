"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { BookOpen } from "lucide-react";
import Input from "@/components/ui/Input";
import Button from "@/components/ui/Button";
import styles from "./page.module.scss";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setTimeout(() => {
      router.push("/admin/dashboard");
    }, 500);
  };

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <div className={styles.logo}>
          <div className={styles.logoIcon}><BookOpen size={22} /></div>
          <span className={styles.logoText}>Library Management</span>
        </div>
        <p className={styles.subtitle}>Đăng nhập Admin / Thủ thư</p>
        <form className={styles.form} onSubmit={handleSubmit}>
          <Input label="Email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="admin@library.edu.vn" required />
          <Input label="Mật khẩu" type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="••••••••" required />
          <Button type="submit" disabled={loading}>{loading ? "Đang đăng nhập..." : "Đăng nhập"}</Button>
        </form>
      </div>
    </div>
  );
}
