import Card from "@/components/ui/Card";
import pageStyles from "@/styles/page.module.scss";

export default function SettingsPage() {
  return (
    <>
      <div className={pageStyles.pageHeader}>
        <h2 className={pageStyles.pageTitle}>Cài đặt</h2>
      </div>
      <Card title="Cài đặt hệ thống">
        <p style={{ fontSize: 14, color: "var(--text-secondary)" }}>
          Trang cài đặt sẽ được cập nhật khi kết nối backend NestJS.
        </p>
      </Card>
    </>
  );
}
