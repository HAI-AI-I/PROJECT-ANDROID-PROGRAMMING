import { Module } from '@nestjs/common';
import { createObserveModule } from '@nestjs/observe';
import { AppController } from './app.controller.js';
import { AppService } from './app.service.js';
import { BooksModule } from './books/books.module.js';
import { ReadersModule } from './readers/readers.module.js';
import { UsersModule } from './users/users.module.js';
import { BorrowingsModule } from './borrowings/borrowings.module.js';
import { ReturnsModule } from './returns/returns.module.js';
import { HistoryModule } from './history/history.module.js';
import { NotificationsModule } from './notifications/notifications.module.js';
import { SupportModule } from './support/support.module.js';
import { DashboardModule } from './dashboard/dashboard.module.js';
import { StatisticsModule } from './statistics/statistics.module.js';

export const { ObserveModule, ObserveInstrument } = createObserveModule();

@Module({
  imports: [
    ObserveModule.forRoot({
      appKey: 'YOUR_APP_KEY',
      appSecret: 'YOUR_APP_SECRET',
      serviceId: 'backend',
    }),
    BooksModule,
    ReadersModule,
    UsersModule,
    BorrowingsModule,
    ReturnsModule,
    HistoryModule,
    NotificationsModule,
    SupportModule,
    DashboardModule,
    StatisticsModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
