import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateReaderDto } from './dto/create-reader.dto.js';
import { UpdateReaderDto } from './dto/update-reader.dto.js';
import { Reader } from './entities/reader.entity.js';

@Injectable()
export class ReadersService {
  private readonly readers: Reader[] = [
    { id: 1, userId: 1, fullName: 'Nguyễn Văn An', email: 'an@gmail.com', phone: '0901111111', avatar: 'https://i.pravatar.cc/150?img=12', status: 'active', totalBorrowing: 5, createdAt: new Date().toISOString() },
    { id: 2, userId: 2, fullName: 'Trần Thị Bình', email: 'binh@gmail.com', phone: '0902222222', avatar: 'https://i.pravatar.cc/150?img=32', status: 'active', totalBorrowing: 3, createdAt: new Date().toISOString() },
    { id: 3, userId: 3, fullName: 'Lê Hoàng Cường', email: 'cuong@gmail.com', phone: '0903333333', avatar: 'https://i.pravatar.cc/150?img=44', status: 'inactive', totalBorrowing: 2, createdAt: new Date().toISOString() },
    { id: 4, userId: 4, fullName: 'Phạm Mỹ Duyên', email: 'duyen@gmail.com', phone: '0904444444', avatar: 'https://i.pravatar.cc/150?img=52', status: 'active', totalBorrowing: 4, createdAt: new Date().toISOString() },
    { id: 5, userId: 5, fullName: 'Hoàng Đức Huy', email: 'huy@gmail.com', phone: '0905555555', avatar: 'https://i.pravatar.cc/150?img=15', status: 'blocked', totalBorrowing: 1, createdAt: new Date().toISOString() },
    { id: 6, userId: 6, fullName: 'Đỗ Thị Lan', email: 'lan@gmail.com', phone: '0906666666', avatar: 'https://i.pravatar.cc/150?img=68', status: 'active', totalBorrowing: 6, createdAt: new Date().toISOString() },
    { id: 7, userId: 7, fullName: 'Vũ Quốc Nam', email: 'nam@gmail.com', phone: '0907777777', avatar: 'https://i.pravatar.cc/150?img=21', status: 'active', totalBorrowing: 2, createdAt: new Date().toISOString() },
    { id: 8, userId: 8, fullName: 'Mai Thu Thảo', email: 'thao@gmail.com', phone: '0908888888', avatar: 'https://i.pravatar.cc/150?img=27', status: 'active', totalBorrowing: 7, createdAt: new Date().toISOString() },
  ];
  private nextId = this.readers.length + 1;

  findAll(search?: string) {
    let items = [...this.readers];
    if (search) {
      const q = search.toLowerCase();
      items = items.filter((reader) => reader.fullName.toLowerCase().includes(q) || reader.email.toLowerCase().includes(q));
    }
    return items;
  }

  findOne(id: number) {
    const reader = this.readers.find((item) => item.id === id);
    if (!reader) throw new NotFoundException('Reader not found');
    return reader;
  }

  create(dto: CreateReaderDto) {
    const reader: Reader = {
      id: this.nextId++,
      userId: dto.userId,
      fullName: dto.fullName,
      email: dto.email,
      phone: dto.phone,
      avatar: dto.avatar,
      status: dto.status ?? 'active',
      totalBorrowing: 0,
      createdAt: new Date().toISOString(),
    };
    this.readers.unshift(reader);
    return reader;
  }

  update(id: number, dto: UpdateReaderDto) {
    const index = this.readers.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Reader not found');
    const updated = { ...this.readers[index], ...dto };
    this.readers[index] = updated;
    return updated;
  }

  remove(id: number) {
    const index = this.readers.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('Reader not found');
    const [deleted] = this.readers.splice(index, 1);
    return deleted;
  }
}
