"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { BookOpen, ChevronLeft, ChevronRight, LogOut } from "lucide-react";
import { navGroups } from "@/config/navigation";
import styles from "./AdminSidebar.module.scss";

interface AdminSidebarProps {
  collapsed: boolean;
  mobileOpen: boolean;
  onToggleCollapse: () => void;
  onNavClick?: () => void;
}

export default function AdminSidebar({
  collapsed,
  mobileOpen,
  onToggleCollapse,
  onNavClick,
}: AdminSidebarProps) {
  const pathname = usePathname();
  const router = useRouter();
  const showLabels = !collapsed || mobileOpen;

  const handleLogout = () => {
    onNavClick?.();
    router.push("/login");
  };

  return (
    <aside
      className={`${styles.sidebar} ${collapsed && !mobileOpen ? styles.collapsed : ""} ${mobileOpen ? styles.open : ""}`}
    >
      <div className={styles.logo}>
        <div className={styles.logoIcon}>
          <BookOpen size={20} />
        </div>
        {showLabels && (
          <span className={styles.logoText}>
            Library
            <br />
            Management
          </span>
        )}
      </div>

      <nav className={styles.nav}>
        {navGroups.map((group, gi) => (
          <div key={gi}>
            {group.title && showLabels && (
              <div className={styles.groupTitle}>{group.title}</div>
            )}
            {group.items.map((item) => {
              const isActive =
                pathname === item.href ||
                (item.href !== "/admin/dashboard" &&
                  pathname.startsWith(item.href));
              const Icon = item.icon;
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={`${styles.navItem} ${isActive ? styles.active : ""}`}
                  title={!showLabels ? item.label : undefined}
                  onClick={onNavClick}
                >
                  <Icon size={18} className={styles.icon} />
                  {showLabels && <span>{item.label}</span>}
                </Link>
              );
            })}
          </div>
        ))}
      </nav>

      <div className={styles.footer}>
        <button className={styles.logout} type="button" onClick={handleLogout}>
          <LogOut size={18} />
          {showLabels && <span>Đăng xuất</span>}
        </button>
      </div>

      <button
        className={styles.collapseBtn}
        onClick={onToggleCollapse}
        type="button"
        aria-label={collapsed ? "Mo rong sidebar" : "Thu gon sidebar"}
      >
        {collapsed ? <ChevronRight size={20} /> : <ChevronLeft size={20} />}
      </button>
    </aside>
  );
}
