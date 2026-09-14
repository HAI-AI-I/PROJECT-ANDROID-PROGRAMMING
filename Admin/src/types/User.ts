export type UserRole = "admin" | "librarian" | "reader";
export type UserStatus = "active" | "inactive";

export interface User {
  userId: string;
  name: string;
  email: string;
  role: UserRole;
  status: UserStatus;
  avatar?: string;
  createdDate: string;
}
