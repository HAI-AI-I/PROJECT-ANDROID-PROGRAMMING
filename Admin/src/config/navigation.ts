import {
  BookOpen,
  BookMarked,
  History,
  LayoutDashboard,
  Bell,
  BarChart3,
  HeadphonesIcon,
  RotateCcw,
  Users,
  UserCircle,
  ClipboardCheck,
  type LucideIcon,
} from "lucide-react";

export interface NavItem {
  label: string;
  href: string;
  icon: LucideIcon;
}

export interface NavGroup {
  title?: string;
  items: NavItem[];
}

export const navGroups: NavGroup[] = [
  {
    items: [
      { label: "Tổng quan", href: "/admin/dashboard", icon: LayoutDashboard },
    ],
  },
  {
    title: "Quản lý",
    items: [
      { label: "Sách", href: "/admin/books", icon: BookOpen },
      { label: "Độc giả", href: "/admin/readers", icon: Users },
      { label: "Mượn sách", href: "/admin/borrowings", icon: BookMarked },
      { label: "Yêu cầu mượn", href: "/admin/borrow-requests", icon: ClipboardCheck },
      { label: "Trả sách", href: "/admin/returns", icon: RotateCcw },
      { label: "Lịch sử", href: "/admin/history", icon: History },
    ],
  },
  {
    title: "Hệ thống",
    items: [
      { label: "Người dùng", href: "/admin/users", icon: UserCircle },
      { label: "Thông báo", href: "/admin/notifications", icon: Bell },
      { label: "Thống kê", href: "/admin/statistics", icon: BarChart3 },
      { label: "Hỗ trợ", href: "/admin/support", icon: HeadphonesIcon },
    ],
  },
];

export const pageTitles: Record<string, string> = {
  "/admin/dashboard": "Tổng quan",
  "/admin/books": "Quản lý sách",
  "/admin/books/new": "Thêm sách",
  "/admin/readers": "Quản lý độc giả",
  "/admin/readers/new": "Thêm độc giả",
  "/admin/borrowings": "Quản lý mượn sách",
  "/admin/borrow-requests": "Yêu cầu mượn sách",
  "/admin/returns": "Trả sách",
  "/admin/history": "Lịch sử mượn/trả",
  "/admin/users": "Người dùng",
  "/admin/notifications": "Thông báo",
  "/admin/statistics": "Thống kê",
  "/admin/support": "Hỗ trợ",
  "/admin/profile": "Hồ sơ",
  "/admin/settings": "Cài đặt",
};

export function getPageTitle(pathname: string): string {
  if (pageTitles[pathname]) return pageTitles[pathname];
  if (pathname.match(/\/admin\/books\/[^/]+\/edit$/)) return "Sửa sách";
  if (pathname.match(/\/admin\/books\/[^/]+$/)) return "Chi tiết sách";
  if (pathname.match(/\/admin\/readers\/[^/]+\/edit$/)) return "Sửa độc giả";
  if (pathname.match(/\/admin\/readers\/[^/]+$/)) return "Chi tiết độc giả";
  if (pathname.match(/\/admin\/readers\/new$/)) return "Thêm độc giả";
  if (pathname.match(/\/admin\/support\/[^/]+$/)) return "Chi tiết hỗ trợ";
  return "Admin";
}
