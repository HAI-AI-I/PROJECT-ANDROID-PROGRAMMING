import type { Metadata, Viewport } from "next";
import "@/styles/globals.scss";

export const metadata: Metadata = {
  title: "Library Management Admin",
  description: "Hệ thống quản lý thư viện - Admin Panel",
};

export const viewport: Viewport = {
  width: "device-width",
  initialScale: 1,
  maximumScale: 1,
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="vi">
      <body>{children}</body>
    </html>
  );
}
