export type SupportStatus = "open" | "in_progress" | "resolved" | "closed";

export interface SupportRequest {
  id: string;
  userName: string;
  userEmail: string;
  bookTitle?: string;
  subject: string;
  message: string;
  createdDate: string;
  status: SupportStatus;
  reply?: string;
  repliedDate?: string;
}
