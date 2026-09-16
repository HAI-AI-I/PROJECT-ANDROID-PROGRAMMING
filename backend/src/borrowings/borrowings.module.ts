import { Module } from '@nestjs/common';
import { BorrowingsController } from './borrowings.controller.js';
import { BorrowingsService } from './borrowings.service.js';
import { BooksModule } from '../books/books.module.js';

@Module({
  imports: [BooksModule],
  controllers: [BorrowingsController],
  providers: [BorrowingsService],
  exports: [BorrowingsService],
})
export class BorrowingsModule {}
