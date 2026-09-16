import { Module } from '@nestjs/common';
import { ReturnsController } from './returns.controller.js';
import { ReturnsService } from './returns.service.js';
import { BorrowingsModule } from '../borrowings/borrowings.module.js';
import { BooksModule } from '../books/books.module.js';

@Module({
  imports: [BorrowingsModule, BooksModule],
  controllers: [ReturnsController],
  providers: [ReturnsService],
})
export class ReturnsModule {}
