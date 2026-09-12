export class History {
  id: number;
  userId: number;
  type: 'BORROW' | 'RETURN' | 'EXTEND' | 'CANCEL' | 'FINE';
  description: string;
  createdAt: string;
}
