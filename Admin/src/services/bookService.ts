import { apiClient, toApiId } from "@/services/apiClient";
import type { Book, BookBorrowHistory, BookFormData } from "@/types/Book";

export interface BookFilters {
  search?: string;
  category?: string;
  status?: string;
  page?: number;
  pageSize?: number;
}

export interface PaginatedBooks {
  items: Book[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export const bookService = {
  async getBooks(filters: BookFilters = {}): Promise<PaginatedBooks> {
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 10;
    const params = new URLSearchParams();
    if (filters.search) params.set("search", filters.search);
    if (filters.category) params.set("category", filters.category);
    if (filters.status) params.set("status", filters.status);
    const books = await apiClient.get<ApiBook[]>(`/books?${params.toString()}`);
    const filtered = books.map(mapBook);
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    const items = filtered.slice(start, start + pageSize);

    return { items, total, page, pageSize, totalPages };
  },

  async getBookById(id: string): Promise<Book | null> {
    try {
      return mapBook(await apiClient.get<ApiBook>(`/books/${toApiId(id)}`));
    } catch {
      return null;
    }
  },

  async getBookHistory(id: string): Promise<BookBorrowHistory[]> {
    return [];
  },

  async createBook(data: BookFormData): Promise<Book> {
    const book = await apiClient.post<ApiBook>("/books", toApiBook(data));
    return mapBook(book);
  },

  async updateBook(id: string, data: BookFormData): Promise<Book | null> {
    try {
      const book = await apiClient.patch<ApiBook>(`/books/${toApiId(id)}`, toApiBook(data));
      return mapBook(book);
    } catch {
      return null;
    }
  },

  async deleteBook(id: string): Promise<boolean> {
    try {
      await apiClient.delete(`/books/${toApiId(id)}`);
      return true;
    } catch {
      return false;
    }
  },
};

interface ApiBook {
  id: number;
  title: string;
  author: string;
  category: string;
  publisher: string;
  publishYear: number;
  quantity: number;
  availableQuantity: number;
  cover?: string;
}

function mapBook(book: ApiBook): Book {
  return { ...book, bookId: `B-${String(book.id).padStart(3, "0")}` };
}

function toApiBook(data: BookFormData) {
  return {
    title: data.title.trim(),
    author: data.author.trim(),
    category: data.category,
    publisher: data.publisher.trim(),
    publishYear: Number(data.publishYear),
    quantity: Number(data.quantity),
    cover: data.cover,
  };
}
