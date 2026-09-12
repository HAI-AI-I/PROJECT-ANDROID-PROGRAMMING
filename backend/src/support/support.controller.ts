import { Body, Controller, Delete, Get, Param, ParseIntPipe, Patch, Post } from '@nestjs/common';
import { SupportService } from './support.service.js';
import { CreateSupportDto } from './dto/create-support.dto.js';
import { UpdateSupportDto } from './dto/update-support.dto.js';

@Controller('support')
export class SupportController {
  constructor(private readonly supportService: SupportService) {}

  @Get()
  findAll() {
    return this.supportService.findAll();
  }

  @Get(':id')
  findOne(@Param('id', ParseIntPipe) id: number) {
    return this.supportService.findOne(id);
  }

  @Post()
  create(@Body() dto: CreateSupportDto) {
    return this.supportService.create(dto);
  }

  @Patch(':id')
  update(@Param('id', ParseIntPipe) id: number, @Body() dto: UpdateSupportDto) {
    return this.supportService.update(id, dto);
  }

  @Delete(':id')
  remove(@Param('id', ParseIntPipe) id: number) {
    return this.supportService.remove(id);
  }

  @Patch(':id/reply')
  reply(@Param('id', ParseIntPipe) id: number, @Body('adminReply') adminReply: string) {
    return this.supportService.reply(id, adminReply);
  }

  @Patch(':id/resolve')
  resolve(@Param('id', ParseIntPipe) id: number) {
    return this.supportService.resolve(id);
  }

  @Patch(':id/close')
  close(@Param('id', ParseIntPipe) id: number) {
    return this.supportService.close(id);
  }
}
