export class Book {
  id: number;
  title: string;
  author: string;
  category: string;
  publisher: string;
  publishYear: number;
  quantity: number;
  availableQuantity: number;
  status: 'available' | 'borrowed' | 'out_of_stock';
  cover?: string;
  createdAt: string;
  updatedAt: string;
}
