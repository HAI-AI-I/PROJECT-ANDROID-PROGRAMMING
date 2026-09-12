import { Injectable, NotFoundException } from '@nestjs/common';

@Injectable()
export class HistoryService {
  private readonly histories = [
    { id: 1, userId: 1, type: 'BORROW', description: 'Nguyễn Văn An đã mượn Clean Architecture', createdAt: '2026-09-12T08:00:00Z' },
    { id: 2, userId: 2, type: 'RETURN', description: 'Trần Thị Bình đã trả Design Patterns', createdAt: '2026-09-11T06:00:00Z' },
    { id: 3, userId: 3, type: 'FINE', description: 'Lê Hoàng Cường bị phạt 20.000đ', createdAt: '2026-09-10T10:00:00Z' },
    { id: 4, userId: 4, type: 'EXTEND', description: 'Phạm Mỹ Duyên gia hạn sách', createdAt: '2026-09-09T14:00:00Z' },
  ];

  findAll(type?: string, userId?: number) {
    let items = [...this.histories];
    if (type) items = items.filter((item) => item.type === type);
    if (userId) items = items.filter((item) => item.userId === userId);
    return items;
  }

  findOne(id: number) {
    const item = this.histories.find((history) => history.id === id);
    if (!item) throw new NotFoundException('History not found');
    return item;
  }
}
