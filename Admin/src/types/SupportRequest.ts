export type SupportStatus = "open" | "in_progress" | "resolved" | "closed";

export interface SupportRequest {
  id: string;
  userName: string;
  userEmail: string;
  subject: string;
  message: string;
  createdDate: string;
  status: SupportStatus;
  reply?: string;
}
