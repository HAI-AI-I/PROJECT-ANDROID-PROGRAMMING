"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { BookOpen } from "lucide-react";
import Input from "@/components/ui/Input";
import Button from "@/components/ui/Button";
import { apiClient } from "@/services/apiClient";
import styles from "./page.module.scss";

interface AuthResponse {
  accessToken: string;
  user: { role: string };
}

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const result = await apiClient.post<AuthResponse>("/auth/login", { identifier: email, password });
      window.localStorage.setItem("library_access_token", result.accessToken);
      window.localStorage.setItem("library_user", JSON.stringify(result.user));
      router.push("/admin/dashboard");
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : "Đăng nhập thất bại");
    } finally {
      setLoading(false);
    }
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
          {error && <p role="alert" style={{ color: "var(--danger)" }}>{error}</p>}
          <Button type="submit" disabled={loading}>{loading ? "Đang đăng nhập..." : "Đăng nhập"}</Button>
        </form>
      </div>
    </div>
  );
}
