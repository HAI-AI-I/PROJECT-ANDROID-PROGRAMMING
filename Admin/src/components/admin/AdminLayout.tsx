"use client";

import { useEffect, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import AdminSidebar from "./AdminSidebar";
import AdminHeader from "./AdminHeader";
import LoadingState from "@/components/ui/LoadingState";
import { getPageTitle } from "@/config/navigation";
import { AUTH_UNAUTHORIZED_EVENT } from "@/services/apiClient";
import { authService } from "@/services/authService";
import styles from "./AdminLayout.module.scss";

interface AdminLayoutProps {
  children: React.ReactNode;
}

export default function AdminLayout({ children }: AdminLayoutProps) {
  const pathname = usePathname();
  const router = useRouter();
  const [collapsed, setCollapsed] = useState(false);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [authorized, setAuthorized] = useState(false);

  const title = getPageTitle(pathname);

  useEffect(() => {
    let active = true;

    const requireAdminSession = async () => {
      const isAdmin = await authService.validateAdminSession();
      if (!active) return;
      if (!isAdmin) {
        setAuthorized(false);
        router.replace("/login");
        return;
      }
      setAuthorized(true);
    };

    const handleUnauthorized = () => {
      setAuthorized(false);
      router.replace("/login");
    };

    void requireAdminSession();
    window.addEventListener(AUTH_UNAUTHORIZED_EVENT, handleUnauthorized);
    return () => {
      active = false;
      window.removeEventListener(AUTH_UNAUTHORIZED_EVENT, handleUnauthorized);
    };
  }, [router]);

  useEffect(() => {
    setMobileOpen(false);
  }, [pathname]);

  useEffect(() => {
    document.body.style.overflow = mobileOpen ? "hidden" : "";
    return () => { document.body.style.overflow = ""; };
  }, [mobileOpen]);

  useEffect(() => {
    const handleResize = () => {
      const w = window.innerWidth;
      if (w <= 768) {
        setCollapsed(false);
      } else {
        setCollapsed(false);
      }
    };
    handleResize();
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const handleLogout = async () => {
    await authService.logout();
    router.replace("/login");
  };

  if (!authorized) return <LoadingState />;

  return (
    <div className={styles.layout}>
      <AdminSidebar
        collapsed={collapsed}
        mobileOpen={mobileOpen}
        onToggleCollapse={() => setCollapsed(!collapsed)}
        onNavClick={() => setMobileOpen(false)}
        onLogout={handleLogout}
      />

      {mobileOpen && (
        <div
          className={styles.overlay}
          onClick={() => setMobileOpen(false)}
          role="presentation"
        />
      )}

      <div className={`${styles.main} ${collapsed ? styles.collapsed : ""}`}>
        <AdminHeader
          title={title}
          onMenuClick={() => setMobileOpen(!mobileOpen)}
          onLogout={handleLogout}
        />
        <main className={styles.content}>{children}</main>
      </div>
    </div>
  );
}
