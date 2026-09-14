import { Controller, Get } from '@nestjs/common';
import { DashboardService } from './dashboard.service.js';

@Controller('dashboard')
export class DashboardController {
  constructor(private readonly dashboardService: DashboardService) {}

  @Get()
  getSummary() {
    return this.dashboardService.getSummary();
  }

  @Get('recent-borrowings')
  getRecentBorrowings() {
    return this.dashboardService.getRecentBorrowings();
  }

  @Get('overdue-books')
  getOverdueBooks() {
    return this.dashboardService.getOverdueBooks();
  }

  @Get('recent-activities')
  getRecentActivities() {
    return this.dashboardService.getRecentActivities();
  }
}
