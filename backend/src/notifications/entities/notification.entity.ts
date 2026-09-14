export class Notification {
  id: number;
  userId: number;
  title: string;
  message: string;
  type: 'BORROW' | 'RETURN' | 'OVERDUE' | 'SYSTEM' | 'SUPPORT';
  isRead: boolean;
  createdAt: string;
}
