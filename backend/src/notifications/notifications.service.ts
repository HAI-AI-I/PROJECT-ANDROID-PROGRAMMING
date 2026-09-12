import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateNotificationDto } from './dto/create-notification.dto.js';
import { UpdateNotificationDto } from './dto/update-notification.dto.js';
import { Notification } from './entities/notification.entity.js';

@Injectable()
export class NotificationsService {
  private readonly notifications: Notification[] = [
    { id: 1, userId: 1, title: 'Mượn sách', message: 'Bạn đã mượn thành công Clean Architecture', type: 'BORROW', isRead: false, createdAt: new Date().toISOString() },
    { id: 2, userId: 1, title: 'Trả sách', message: 'Sách của bạn đã được xác nhận trả', type: 'RETURN', isRead: true, createdAt: new Date().toISOString() },
    { id: 3, userId: 2, title: 'Quá hạn', message: 'Bạn đang quá hạn 2 ngày', type: 'OVERDUE', isRead: false, createdAt: new Date().toISOString() },
  ];

  private nextId = this.notifications.length + 1;

  findAll() {
    return [...this.notifications];
  }

  findOne(id: number) {
    const item = this.notifications.find((notification) => notification.id === id);
    if (!item) throw new NotFoundException('Notification not found');
    return item;
  }

  create(dto: CreateNotificationDto) {
    const item: Notification = {
      id: this.nextId++,
      userId: dto.userId,
      title: dto.title,
      message: dto.message,
      type: dto.type,
      isRead: dto.isRead ?? false,
      createdAt: new Date().toISOString(),
    };
    this.notifications.unshift(item);
    return item;
  }

  update(id: number, dto: UpdateNotificationDto) {
    const index = this.notifications.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Notification not found');
    const updated = { ...this.notifications[index], ...dto };
    this.notifications[index] = updated;
    return updated;
  }

  remove(id: number) {
    const index = this.notifications.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Notification not found');
    const [deleted] = this.notifications.splice(index, 1);
    return deleted;
  }

  markRead(id: number) {
    const item = this.findOne(id);
    item.isRead = true;
    return item;
  }

  markReadAll() {
    this.notifications.forEach((item) => {
      item.isRead = true;
    });
    return this.notifications;
  }
}
