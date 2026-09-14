import { Body, Controller, Delete, Get, Param, ParseIntPipe, Patch, Post } from '@nestjs/common';
import { BorrowingsService } from './borrowings.service.js';
import { CreateBorrowingDto } from './dto/create-borrowing.dto.js';
import { UpdateBorrowingDto } from './dto/update-borrowing.dto.js';

@Controller('borrowings')
export class BorrowingsController {
  constructor(private readonly borrowingsService: BorrowingsService) {}

  @Get()
  findAll() {
    return this.borrowingsService.findAll();
  }

  @Get(':id')
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.borrowingsService.findOne(id);
  }

  @Post()
  create(@Body() dto: CreateBorrowingDto) {
    return this.borrowingsService.create(dto);
  }

  @Patch(':id')
  update(@Param('id', ParseIntPipe) id: number, @Body() dto: UpdateBorrowingDto) {
    return this.borrowingsService.update(id, dto);
  }

  @Delete(':id')
  remove(@Param('id', ParseIntPipe) id: number) {
    return this.borrowingsService.remove(id);
  }

  @Patch(':id/approve')
  approve(@Param('id', ParseIntPipe) id: number) {
    return this.borrowingsService.approve(id);
  }

  @Patch(':id/reject')
  reject(@Param('id', ParseIntPipe) id: number) {
    return this.borrowingsService.reject(id);
  }
}
