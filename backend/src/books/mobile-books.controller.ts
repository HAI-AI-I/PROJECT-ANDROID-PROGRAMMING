import { Controller, Get, Query } from '@nestjs/common';
import { BooksService } from './books.service.js';
import { Book } from './entities/book.entity.js';

@Controller('v1/books')
export class MobileBooksController {
  constructor(private readonly booksService: BooksService) {}

  @Get('latest')
  latest(@Query('limit') limit = 10) {
    return this.booksService
      .findAll()
      .slice(0, this.normalizeLimit(limit))
      .map((book) => this.toMobileBook(book));
  }

  @Get('popular')
  popular(@Query('limit') limit = 10) {
    return this.booksService
      .findAll()
      .slice(0, this.normalizeLimit(limit))
      .map((book, index) => ({
        book: this.toMobileBook(book),
        borrowCount: Math.max(0, book.quantity - book.availableQuantity),
        favoriteCount: Math.max(0, 20 - index),
        notificationClickCount: 0,
        popularityScore: Math.max(1, 100 - index * 5),
      }));
  }

  private toMobileBook(book: Book) {
    return {
      id: book.id,
      title: book.title,
      author: book.author,
      category: book.category,
      cover: book.cover ?? null,
      borrowFee: 0,
      availableQuantity: book.availableQuantity,
      rating: 4.5,
      ratingCount: 0,
      createdAt: book.createdAt,
    };
  }

  private normalizeLimit(value: number) {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? Math.min(Math.max(parsed, 1), 50) : 10;
  }
}
