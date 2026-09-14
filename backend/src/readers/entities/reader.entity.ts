export class Reader {
  id: number;
  userId: number;
  fullName: string;
  email: string;
  phone: string;
  avatar?: string;
  status: 'active' | 'inactive' | 'blocked';
  totalBorrowing: number;
  createdAt: string;
}
