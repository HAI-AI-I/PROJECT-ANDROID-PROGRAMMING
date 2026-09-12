import { Body, Controller, Delete, Get, Param, ParseIntPipe, Patch, Post, Query } from '@nestjs/common';
import { ReadersService } from './readers.service.js';
import { CreateReaderDto } from './dto/create-reader.dto.js';
import { UpdateReaderDto } from './dto/update-reader.dto.js';

@Controller('readers')
export class ReadersController {
  constructor(private readonly readersService: ReadersService) {}

  @Get()
  findAll(@Query('search') search?: string) {
    return this.readersService.findAll(search);
  }

  @Get(':id')
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.readersService.findOne(id);
  }

  @Post()
  create(@Body() dto: CreateReaderDto) {
    return this.readersService.create(dto);
  }

  @Patch(':id')
  update(@Param('id', ParseIntPipe) id: number, @Body() dto: UpdateReaderDto) {
    return this.readersService.update(id, dto);
  }

  @Delete(':id')
  remove(@Param('id', ParseIntPipe) id: number) {
    return this.readersService.remove(id);
  }
}
