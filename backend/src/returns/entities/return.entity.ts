export class ReturnRecord {
  id: number;
  borrowingId: number;
  returnDate: string;
  condition: 'GOOD' | 'DAMAGED' | 'LOST';
  fine: number;
  note?: string;
  status: 'PENDING' | 'CONFIRMED';
}
