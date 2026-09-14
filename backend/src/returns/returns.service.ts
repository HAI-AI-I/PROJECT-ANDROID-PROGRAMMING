import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateReturnDto } from './dto/create-return.dto.js';
import { UpdateReturnDto } from './dto/update-return.dto.js';
import { ReturnRecord } from './entities/return.entity.js';

@Injectable()
export class ReturnsService {
  private readonly returns: ReturnRecord[] = [
    { id: 1, borrowingId: 4, returnDate: '2026-09-12', condition: 'GOOD', fine: 0, note: 'Tốt', status: 'CONFIRMED' },
  ];

  private nextId = this.returns.length + 1;

  findAll() {
    return [...this.returns];
  }

  findOne(id: number) {
    const record = this.returns.find((item) => item.id === id);
    if (!record) throw new NotFoundException('Return record not found');
    return record;
  }

  create(dto: CreateReturnDto) {
    const record: ReturnRecord = {
      id: this.nextId++,
      borrowingId: dto.borrowingId,
      returnDate: dto.returnDate,
      condition: dto.condition,
      fine: dto.fine,
      note: dto.note,
      status: 'PENDING',
    };
    this.returns.unshift(record);
    return record;
  }

  update(id: number, dto: UpdateReturnDto) {
    const index = this.returns.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Return record not found');
    const updated = { ...this.returns[index], ...dto };
    this.returns[index] = updated;
    return updated;
  }

  remove(id: number) {
    const index = this.returns.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Return record not found');
    const [deleted] = this.returns.splice(index, 1);
    return deleted;
  }

  confirm(id: number) {
    const record = this.findOne(id);
    record.status = 'CONFIRMED';
    return record;
  }
}
