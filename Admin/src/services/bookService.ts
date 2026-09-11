import { initialBooks, bookBorrowHistory } from "@/data/books";
import type { Book, BookBorrowHistory, BookFormData } from "@/types/Book";

let booksStore: Book[] = [...initialBooks];

const delay = (ms = 300) => new Promise((resolve) => setTimeout(resolve, ms));

function generateId(): string {
  const num = booksStore.length + 1;
  return `B-${String(num).padStart(3, "0")}`;
}

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

function filterBooks(filters: BookFilters): Book[] {
  let result = [...booksStore];

  if (filters.search) {
    const q = filters.search.toLowerCase();
    result = result.filter(
      (b) =>
        b.title.toLowerCase().includes(q) ||
        b.author.toLowerCase().includes(q) ||
        b.bookId.toLowerCase().includes(q)
    );
  }

  if (filters.category) {
    result = result.filter((b) => b.category === filters.category);
  }

  if (filters.status) {
    result = result.filter((b) => {
      const status =
        b.availableQuantity === 0
          ? "out_of_stock"
          : b.availableQuantity < b.quantity
            ? "borrowed"
            : "available";
      return status === filters.status;
    });
  }

  return result;
}

export const bookService = {
  async getBooks(filters: BookFilters = {}): Promise<PaginatedBooks> {
    await delay();
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 10;
    const filtered = filterBooks(filters);
    const total = filtered.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    const start = (page - 1) * pageSize;
    const items = filtered.slice(start, start + pageSize);

    return { items, total, page, pageSize, totalPages };
  },

  async getBookById(id: string): Promise<Book | null> {
    await delay();
    return booksStore.find((b) => b.bookId === id) ?? null;
  },

  async getBookHistory(id: string): Promise<BookBorrowHistory[]> {
    await delay();
    return bookBorrowHistory[id] ?? [];
  },

  async createBook(data: BookFormData): Promise<Book> {
    await delay();
    const quantity = parseInt(data.quantity, 10);
    const book: Book = {
      bookId: generateId(),
      title: data.title.trim(),
      author: data.author.trim(),
      category: data.category,
      publisher: data.publisher.trim(),
      publishYear: parseInt(data.publishYear, 10),
      quantity,
      availableQuantity: quantity,
      cover: data.cover,
    };
    booksStore = [book, ...booksStore];
    return book;
  },

  async updateBook(id: string, data: BookFormData): Promise<Book | null> {
    await delay();
    const index = booksStore.findIndex((b) => b.bookId === id);
    if (index === -1) return null;

    const old = booksStore[index];
    const quantity = parseInt(data.quantity, 10);
    const borrowed = old.quantity - old.availableQuantity;
    const availableQuantity = Math.max(0, quantity - borrowed);

    const updated: Book = {
      ...old,
      title: data.title.trim(),
      author: data.author.trim(),
      category: data.category,
      publisher: data.publisher.trim(),
      publishYear: parseInt(data.publishYear, 10),
      quantity,
      availableQuantity,
      cover: data.cover ?? old.cover,
    };
    booksStore[index] = updated;
    return updated;
  },

  async deleteBook(id: string): Promise<boolean> {
    await delay();
    const len = booksStore.length;
    booksStore = booksStore.filter((b) => b.bookId !== id);
    return booksStore.length < len;
  },
};
