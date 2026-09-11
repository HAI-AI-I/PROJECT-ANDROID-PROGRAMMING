"use client";

import { useState, useRef, useEffect } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Bell, ChevronDown, Menu, Search } from "lucide-react";
import { notificationService } from "@/services/notificationService";
import styles from "./AdminHeader.module.scss";

interface AdminHeaderProps {
  title: string;
  onMenuClick: () => void;
}

export default function AdminHeader({ title, onMenuClick }: AdminHeaderProps) {
  const router = useRouter();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [hasUnreadNotifications, setHasUnreadNotifications] = useState(false);
  const profileRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    let active = true;

    const refreshUnreadState = async () => {
      const notifications = await notificationService.getNotifications();
      if (!active) return;
      setHasUnreadNotifications(notifications.some((notification) => !notification.isRead));
    };

    refreshUnreadState();
    window.addEventListener("notifications:changed", refreshUnreadState);
    return () => {
      active = false;
      window.removeEventListener("notifications:changed", refreshUnreadState);
    };
  }, []);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (profileRef.current && !profileRef.current.contains(e.target as Node)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    setDropdownOpen(false);
    router.push("/login");
  };

  return (
    <header className={styles.header}>
      <div className={styles.left}>
        <button
          className={styles.menuBtn}
          onClick={onMenuClick}
          type="button"
          aria-label="Mo menu"
        >
          <Menu size={18} />
        </button>
        <h1 className={styles.pageTitle}>{title}</h1>
      </div>

      <div className={styles.right}>
        <div className={styles.search}>
          <Search size={16} color="var(--text-tertiary)" />
          <input type="text" placeholder="Tìm kiếm..." />
        </div>

        <Link
          href="/admin/notifications"
          className={styles.iconBtn}
          aria-label="Thong bao"
        >
          <Bell size={18} />
          {hasUnreadNotifications && <span className={styles.badge} />}
        </Link>

        <div className={styles.profile} ref={profileRef}>
          <button
            className={styles.avatarBtn}
            onClick={() => setDropdownOpen(!dropdownOpen)}
            type="button"
          >
            <div className={styles.avatar}>AD</div>
            <span>Admin</span>
            <ChevronDown size={14} />
          </button>

          {dropdownOpen && (
            <div className={styles.dropdown}>
              <Link
                href="/admin/profile"
                className={styles.dropdownItem}
                onClick={() => setDropdownOpen(false)}
              >
                Hồ sơ
              </Link>
              <Link
                href="/admin/settings"
                className={styles.dropdownItem}
                onClick={() => setDropdownOpen(false)}
              >
                Cài đặt
              </Link>
              <button
                className={styles.dropdownItem}
                type="button"
                onClick={handleLogout}
              >
                Đăng xuất
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
