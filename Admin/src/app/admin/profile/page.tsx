"use client";

import { useCallback, useEffect, useState } from "react";
import { Mail, Phone, ShieldCheck, UserRound } from "lucide-react";
import Card from "@/components/ui/Card";
import ErrorState from "@/components/ui/ErrorState";
import LoadingState from "@/components/ui/LoadingState";
import Badge from "@/components/ui/Badge";
import { authService, type AuthUser } from "@/services/authService";
import pageStyles from "@/styles/page.module.scss";
import styles from "./profile.module.scss";

export default function ProfilePage() {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const fetchProfile = useCallback(async () => {
    setLoading(true);
    setError(false);
    try {
      setUser(await authService.getCurrentUser());
    } catch {
      setError(true);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void fetchProfile();
  }, [fetchProfile]);

  return (
    <>
      <div className={pageStyles.pageHeader}>
        <h2 className={pageStyles.pageTitle}>Hồ sơ</h2>
      </div>
      <Card title="Thông tin tài khoản">
        {loading ? (
          <LoadingState />
        ) : error || !user ? (
          <ErrorState
            title="Không thể tải hồ sơ"
            description="Không lấy được thông tin tài khoản từ máy chủ."
            onRetry={fetchProfile}
          />
        ) : (
          <div className={styles.profile}>
            <div className={styles.identity}>
              <div className={styles.avatar} aria-hidden="true">
                {user.fullName.trim().charAt(0).toUpperCase() || "A"}
              </div>
              <div>
                <h3>{user.fullName}</h3>
                <p>Mã tài khoản: U-{String(user.id).padStart(3, "0")}</p>
              </div>
              <Badge variant={user.active ? "success" : "danger"}>
                {user.active ? "Đang hoạt động" : "Đã vô hiệu hóa"}
              </Badge>
            </div>

            <div className={styles.details}>
              <ProfileField icon={<Mail size={18} />} label="Email" value={user.email} />
              <ProfileField icon={<Phone size={18} />} label="Số điện thoại" value={user.phone || "Chưa cập nhật"} />
              <ProfileField icon={<ShieldCheck size={18} />} label="Vai trò" value="Quản trị viên" />
              <ProfileField icon={<UserRound size={18} />} label="Họ và tên" value={user.fullName} />
            </div>
          </div>
        )}
      </Card>
    </>
  );
}

function ProfileField({ icon, label, value }: {
  icon: React.ReactNode;
  label: string;
  value: string;
}) {
  return (
    <div className={styles.field}>
      <span className={styles.icon}>{icon}</span>
      <div>
        <p className={styles.label}>{label}</p>
        <p className={styles.value}>{value}</p>
      </div>
    </div>
  );
}
