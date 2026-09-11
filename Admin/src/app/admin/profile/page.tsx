import Card from "@/components/ui/Card";
import pageStyles from "@/styles/page.module.scss";

export default function ProfilePage() {
  return (
    <>
      <div className={pageStyles.pageHeader}>
        <h2 className={pageStyles.pageTitle}>Hồ sơ</h2>
      </div>
      <Card title="Thông tin tài khoản">
        <div style={{ display: "grid", gap: 12, fontSize: 14 }}>
          <p><strong>Tên:</strong> Admin</p>
          <p><strong>Email:</strong> admin@library.edu.vn</p>
          <p><strong>Vai trò:</strong> Administrator</p>
        </div>
      </Card>
    </>
  );
}
