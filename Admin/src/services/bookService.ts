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

export interface BookCategory {
  id: number;
  name: string;
  slug: string;
  active: boolean;
}

export interface BookCoverUpload {
  fileName: string;
  path: string;
  url: string;
}

export const bookService = {
  async getBooks(filters: BookFilters = {}): Promise<PaginatedBooks> {
    const page = filters.page ?? 1;
    const pageSize = filters.pageSize ?? 10;
    const params = new URLSearchParams();
    if (filters.search) params.set("search", filters.search);
    if (filters.category) params.set("category", filters.category);
    if (filters.status) params.set("status", filters.status);
    params.set("page", String(page));
    params.set("pageSize", String(pageSize));
    const response = await apiClient.get<ApiPagedBooks>(`/books?${params.toString()}`);
    return {
      items: response.items.map((book) => mapBook(book)),
      total: response.total,
      page: response.page,
      pageSize: response.pageSize,
      totalPages: response.totalPages,
    };
  },

  async getCategories(): Promise<BookCategory[]> {
    const categories = await apiClient.get<BookCategory[]>("/categories");
    return categories.filter((category) => category.active);
  },

  async uploadBookCover(file: File): Promise<BookCoverUpload> {
    const body = new FormData();
    body.append("file", file);
    return apiClient.postForm<BookCoverUpload>("/admin/book-covers", body);
  },

  async importBookCover(url: string): Promise<BookCoverUpload> {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`Không thể tải ảnh bìa từ nguồn dữ liệu (${response.status})`);
    }

    const blob = await response.blob();
    if (blob.size > 5 * 1024 * 1024) {
      throw new Error("Ảnh bìa không được vượt quá 5 MB");
    }

    const mimeType = blob.type.split(";")[0].toLowerCase();
    const extensionByType: Record<string, string> = {
      "image/jpeg": "jpg",
      "image/png": "png",
      "image/webp": "webp",
    };
    const extension = extensionByType[mimeType];
    if (!extension) {
      throw new Error("Nguồn dữ liệu không trả về ảnh JPEG, PNG hoặc WebP");
    }

    const body = new FormData();
    body.append("file", new File([blob], `book-cover.${extension}`, { type: mimeType }));
    return apiClient.postForm<BookCoverUpload>("/admin/book-covers", body);
  },

  async isIsbnAvailable(isbn: string, excludedBookId?: string): Promise<boolean> {
    const params = new URLSearchParams({ isbn });
    if (excludedBookId) params.set("excludeBookId", String(toApiId(excludedBookId)));
    const result = await apiClient.get<{ isbn: string; available: boolean }>(
      `/admin/books/isbn-availability?${params.toString()}`
    );
    return result.available;
  },

  async getBookById(id: string): Promise<Book | null> {
    try {
      const detail = await apiClient.get<ApiBookDetail>(`/books/${toApiId(id)}/detail`);
      const shelfLocation = detail.copies.find((copy) => copy.shelfLocation?.trim())?.shelfLocation;
      return mapBook(detail.book, shelfLocation);
    } catch {
      return null;
    }
  },

  async getBookHistory(id: string): Promise<BookBorrowHistory[]> {
    const history = await apiClient.get<ApiBorrowOrder[]>(
      `/admin/borrow-orders/books/${toApiId(id)}`
    );
    return history.map(mapBookHistory);
  },

  async createBook(data: BookFormData): Promise<Book> {
    const book = await apiClient.post<ApiBook>("/books", toApiBook(data));
    return mapBook(book);
  },

  async updateBook(id: string, data: BookFormData): Promise<Book> {
    const book = await apiClient.patch<ApiBook>(`/books/${toApiId(id)}`, toApiBook(data));
    return mapBook(book, data.shelfLocation);
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
  isbn?: string;
  title: string;
  author: string;
  category: string;
  publisher?: string;
  publishYear?: number;
  quantity: number;
  availableQuantity: number;
  cover?: string;
  description?: string;
  borrowFee: number;
  authorDetails?: Book["authorDetails"];
  publisherDetails?: Book["publisherDetails"];
}

interface ApiBookDetail {
  book: ApiBook;
  copies: Array<{ shelfLocation?: string }>;
}

interface ApiPagedBooks {
  items: ApiBook[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

interface ApiBorrowOrder {
  referenceCode: string;
  status: string;
  copyBarcode: string;
  borrowerId: number;
  borrowerName: string;
  requestedAt: string;
  borrowedAt?: string;
  dueAt?: string;
  returnedAt?: string;
}

function mapBook(book: ApiBook, shelfLocation?: string): Book {
  return { ...book, shelfLocation, bookId: `B-${String(book.id).padStart(3, "0")}` };
}

function mapBookHistory(order: ApiBorrowOrder): BookBorrowHistory {
  const statusMap: Record<string, BookBorrowHistory["status"]> = {
    PENDING_PAYMENT: "pending_payment",
    REQUESTED: "requested",
    BORROWED: "borrowing",
    RETURNED: "returned",
    OVERDUE: "overdue",
    CANCELLED: "cancelled",
  };
  return {
    id: order.referenceCode,
    readerId: String(order.borrowerId),
    readerName: order.borrowerName,
    copyBarcode: order.copyBarcode,
    requestedDate: formatDateTime(order.requestedAt),
    borrowDate: formatDateTime(order.borrowedAt),
    dueDate: formatDateTime(order.dueAt),
    returnDate: order.returnedAt ? formatDateTime(order.returnedAt) : undefined,
    status: statusMap[order.status] ?? "requested",
  };
}

function formatDateTime(value?: string): string {
  return value ? new Date(value).toLocaleString("vi-VN") : "—";
}

function toApiBook(data: BookFormData) {
  const authorIds = data.authorDetails?.map((author) => ({ id: author.id }));
  const publisherId = data.publisherDetails?.id;
  return {
    isbn: data.isbn.trim(),
    title: data.title.trim(),
    author: authorIds?.length ? null : data.author.trim(),
    authors: authorIds?.length ? authorIds : null,
    category: data.category,
    publisher: publisherId ? null : data.publisher.trim(),
    publisherDetails: publisherId ? { id: publisherId } : null,
    publishYear: Number(data.publishYear),
    quantity: Number(data.quantity),
    cover: data.cover?.trim() ?? "",
    description: data.description.trim(),
    borrowFee: Number(data.borrowFee),
    shelfLocation: data.shelfLocation.trim(),
  };
}
