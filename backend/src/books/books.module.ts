import { Module } from '@nestjs/common';
import { BooksController } from './books.controller.js';
import { MobileBooksController } from './mobile-books.controller.js';
import { BooksService } from './books.service.js';

@Module({
  controllers: [BooksController, MobileBooksController],
  providers: [BooksService],
  exports: [BooksService],
})
export class BooksModule {}
