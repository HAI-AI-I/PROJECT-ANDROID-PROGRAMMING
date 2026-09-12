import { Controller, Get } from '@nestjs/common';
import { StatisticsService } from './statistics.service.js';

@Controller('statistics')
export class StatisticsController {
  constructor(private readonly statisticsService: StatisticsService) {}

  @Get()
  getSummary() {
    return this.statisticsService.getSummary();
  }

  @Get('borrowing-trend')
  getBorrowingTrend() {
    return this.statisticsService.getBorrowingTrend();
  }

  @Get('books-by-category')
  getBooksByCategory() {
    return this.statisticsService.getBooksByCategory();
  }

  @Get('popular-books')
  getPopularBooks() {
    return this.statisticsService.getPopularBooks();
  }

  @Get('active-readers')
  getActiveReaders() {
    return this.statisticsService.getActiveReaders();
  }
}
