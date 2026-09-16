import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateSupportDto } from './dto/create-support.dto.js';
import { UpdateSupportDto } from './dto/update-support.dto.js';
import { SupportRequest } from './entities/support-request.entity.js';
import { PersistentStoreService } from '../persistence/persistent-store.service.js';

@Injectable()
export class SupportService {
  private readonly supportRequests: SupportRequest[] = [
    {
      id: 1,
      userId: 3,
      subject: 'Không thể đăng nhập',
      message: 'Tôi không thể mở app trên thiết bị mới.',
      status: 'OPEN',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
    {
      id: 2,
      userId: 4,
      subject: 'Yêu cầu gia hạn sách',
      message: 'Tôi muốn gia hạn thêm 7 ngày cho sách Design Patterns.',
      status: 'IN_PROGRESS',
      adminReply: 'Đã nhận, đang xử lý.',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    },
  ];

  private nextId = this.supportRequests.length + 1;

  constructor(private readonly store: PersistentStoreService) {
    const persisted = this.store.getCollection<SupportRequest>('supportRequests', this.supportRequests);
    this.supportRequests.splice(0, this.supportRequests.length, ...persisted);
    this.nextId = Math.max(0, ...this.supportRequests.map((item) => item.id)) + 1;
  }

  private persist() {
    this.store.saveCollection('supportRequests', this.supportRequests);
  }

  findAll() {
    return [...this.supportRequests];
  }

  findOne(id: number) {
    const item = this.supportRequests.find((request) => request.id === id);
    if (!item) throw new NotFoundException('Support request not found');
    return item;
  }

  create(dto: CreateSupportDto) {
    const item: SupportRequest = {
      id: this.nextId++,
      userId: dto.userId,
      subject: dto.subject,
      message: dto.message,
      status: 'OPEN',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    this.supportRequests.unshift(item);
    this.persist();
    return item;
  }

  update(id: number, dto: UpdateSupportDto) {
    const index = this.supportRequests.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Support request not found');
    const updated = { ...this.supportRequests[index], ...dto, updatedAt: new Date().toISOString() };
    this.supportRequests[index] = updated;
    this.persist();
    return updated;
  }

  remove(id: number) {
    const index = this.supportRequests.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Support request not found');
    const [deleted] = this.supportRequests.splice(index, 1);
    this.persist();
    return deleted;
  }

  reply(id: number, adminReply: string) {
    const item = this.findOne(id);
    item.adminReply = adminReply;
    item.status = 'IN_PROGRESS';
    item.updatedAt = new Date().toISOString();
    this.persist();
    return item;
  }

  resolve(id: number) {
    const item = this.findOne(id);
    item.status = 'RESOLVED';
    item.updatedAt = new Date().toISOString();
    this.persist();
    return item;
  }

  close(id: number) {
    const item = this.findOne(id);
    item.status = 'CLOSED';
    item.updatedAt = new Date().toISOString();
    this.persist();
    return item;
  }
}
