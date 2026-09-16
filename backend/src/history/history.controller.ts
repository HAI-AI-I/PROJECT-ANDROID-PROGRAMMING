import { Controller, Get, Param, ParseIntPipe, Query } from '@nestjs/common';
import { HistoryService } from './history.service.js';

@Controller('history')
export class HistoryController {
  constructor(private readonly historyService: HistoryService) {}

  @Get()
  findAll(@Query('type') type?: string, @Query('userId') userId?: string) {
    return this.historyService.findAll(type, userId ? Number(userId) : undefined);
  }

  @Get(':id')
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.historyService.findOne(id);
  }
}
