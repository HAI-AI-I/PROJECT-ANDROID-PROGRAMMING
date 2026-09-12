import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateBookDto } from './dto/create-book.dto.js';
import { UpdateBookDto } from './dto/update-book.dto.js';
import { Book } from './entities/book.entity.js';

@Injectable()
export class BooksService {
  private readonly books: Book[] = [
    {
      id: 1,
      title: 'Clean Architecture',
      author: 'Robert C. Martin',
      category: 'Lập trình',
      publisher: 'Nhã Nam',
      publishYear: 2017,
      quantity: 10,
      availableQuantity: 8,
      status: 'available',
      cover: 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 2,
      title: 'Design Patterns',
      author: 'Gang of Four',
      category: 'Lập trình',
      publisher: 'Pearson',
      publishYear: 1994,
      quantity: 6,
      availableQuantity: 4,
      status: 'borrowed',
      cover: 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 3,
      title: 'Kotlin in Action',
      author: 'Dmitry Jemerov',
      category: 'Lập trình',
      publisher: 'Manning',
      publishYear: 2017,
      quantity: 5,
      availableQuantity: 5,
      status: 'available',
      cover: 'https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 4,
      title: 'Cấu trúc dữ liệu và giải thuật nâng cao',
      author: 'Nguyễn Văn A',
      category: 'Lập trình',
      publisher: 'ĐHQG',
      publishYear: 2021,
      quantity: 12,
      availableQuantity: 7,
      status: 'available',
      cover: 'https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 5,
      title: 'Hệ quản trị cơ sở dữ liệu quan hệ',
      author: 'Trần Thị B',
      category: 'Cơ sở dữ liệu',
      publisher: 'ĐH CNTT',
      publishYear: 2020,
      quantity: 8,
      availableQuantity: 3,
      status: 'borrowed',
      cover: 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 6,
      title: 'Mạng máy tính căn bản',
      author: 'Lê Văn C',
      category: 'Mạng máy tính',
      publisher: 'Bách Khoa',
      publishYear: 2019,
      quantity: 9,
      availableQuantity: 2,
      status: 'borrowed',
      cover: 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 7,
      title: 'Clean Code',
      author: 'Robert C. Martin',
      category: 'Lập trình',
      publisher: 'Prentice Hall',
      publishYear: 2008,
      quantity: 15,
      availableQuantity: 15,
      status: 'available',
      cover: 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 8,
      title: 'Code Đạo Ký Sự',
      author: 'Phạm Huy Hoàng',
      category: 'Lập trình',
      publisher: 'Tổng hợp',
      publishYear: 2022,
      quantity: 7,
      availableQuantity: 7,
      status: 'available',
      cover: 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 9,
      title: 'JavaScript Nâng Cao',
      author: 'Linh Hoàng',
      category: 'Lập trình',
      publisher: 'TechBooks',
      publishYear: 2023,
      quantity: 11,
      availableQuantity: 9,
      status: 'available',
      cover: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 10,
      title: 'Python cho người mới bắt đầu',
      author: 'Minh Đạt',
      category: 'Lập trình',
      publisher: 'CodeLab',
      publishYear: 2024,
      quantity: 13,
      availableQuantity: 0,
      status: 'out_of_stock',
      cover: 'https://images.unsplash.com/photo-1526379095098-d400fd0bf935?auto=format&fit=crop&w=400&q=80',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
  ];

  private nextId = this.books.length + 1;

  getStatus(quantity: number, availableQuantity: number): Book['status'] {
    if (availableQuantity <= 0) return 'out_of_stock';
    if (availableQuantity < quantity) return 'borrowed';
    return 'available';
  }

  findAll(query?: { search?: string; category?: string; status?: string }) {
    let items = [...this.books];

    if (query?.search) {
      const search = query.search.toLowerCase();
      items = items.filter(
        (book) =>
          book.title.toLowerCase().includes(search) ||
          book.author.toLowerCase().includes(search) ||
          book.category.toLowerCase().includes(search),
      );
    }

    if (query?.category) {
      items = items.filter((book) => book.category === query.category);
    }

    if (query?.status) {
      items = items.filter((book) => book.status === query.status);
    }

    return items;
  }

  findOne(id: number) {
    const book = this.books.find((item) => item.id === id);
    if (!book) throw new NotFoundException('Book not found');
    return book;
  }

  create(dto: CreateBookDto) {
    const quantity = Number(dto.quantity);
    const availableQuantity = quantity;
    const now = new Date().toISOString();

    const book: Book = {
      id: this.nextId++,
      title: dto.title,
      author: dto.author,
      category: dto.category,
      publisher: dto.publisher,
      publishYear: Number(dto.publishYear),
      quantity,
      availableQuantity,
      status: this.getStatus(quantity, availableQuantity),
      cover: dto.cover,
      createdAt: now,
      updatedAt: now,
    };

    this.books.unshift(book);
    return book;
  }

  update(id: number, dto: UpdateBookDto) {
    const index = this.books.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Book not found');

    const current = this.books[index];
    const quantity = dto.quantity ?? current.quantity;
    const borrowed = current.quantity - current.availableQuantity;
    const availableQuantity = Math.max(0, quantity - borrowed);

    const updated: Book = {
      ...current,
      ...dto,
      quantity,
      availableQuantity,
      publishYear: dto.publishYear ?? current.publishYear,
      status: this.getStatus(quantity, availableQuantity),
      updatedAt: new Date().toISOString(),
    };

    this.books[index] = updated;
    return updated;
  }

  remove(id: number) {
    const index = this.books.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Book not found');
    const [deleted] = this.books.splice(index, 1);
    return deleted;
  }
}
