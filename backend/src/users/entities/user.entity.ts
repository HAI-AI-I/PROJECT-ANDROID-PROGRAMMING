export class User {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  role: 'ADMIN' | 'LIBRARIAN' | 'READER';
  status: 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
  avatar?: string;
  createdAt: string;
}
