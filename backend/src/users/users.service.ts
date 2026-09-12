import { Injectable, NotFoundException } from '@nestjs/common';
import { CreateUserDto } from './dto/create-user.dto.js';
import { UpdateUserDto } from './dto/update-user.dto.js';
import { User } from './entities/user.entity.js';

@Injectable()
export class UsersService {
  private readonly users: User[] = [
    { id: 1, fullName: 'Admin Library', email: 'admin@library.com', phone: '0901000001', role: 'ADMIN', status: 'ACTIVE', avatar: 'https://i.pravatar.cc/150?img=1', createdAt: new Date().toISOString() },
    { id: 2, fullName: 'Librarian One', email: 'librarian1@library.com', phone: '0901000002', role: 'LIBRARIAN', status: 'ACTIVE', avatar: 'https://i.pravatar.cc/150?img=2', createdAt: new Date().toISOString() },
    { id: 3, fullName: 'Nguyễn Văn An', email: 'an@library.com', phone: '0901000003', role: 'READER', status: 'ACTIVE', avatar: 'https://i.pravatar.cc/150?img=3', createdAt: new Date().toISOString() },
    { id: 4, fullName: 'Trần Thị Bình', email: 'binh@library.com', phone: '0901000004', role: 'READER', status: 'ACTIVE', avatar: 'https://i.pravatar.cc/150?img=4', createdAt: new Date().toISOString() },
    { id: 5, fullName: 'Lê Hoàng Cường', email: 'cuong@library.com', phone: '0901000005', role: 'READER', status: 'INACTIVE', avatar: 'https://i.pravatar.cc/150?img=5', createdAt: new Date().toISOString() },
    { id: 6, fullName: 'Phạm Mỹ Duyên', email: 'duyen@library.com', phone: '0901000006', role: 'READER', status: 'BLOCKED', avatar: 'https://i.pravatar.cc/150?img=6', createdAt: new Date().toISOString() },
  ];

  private nextId = this.users.length + 1;

  findAll(search?: string, role?: string, status?: string) {
    let items = [...this.users];
    if (search) {
      const q = search.toLowerCase();
      items = items.filter((user) => user.fullName.toLowerCase().includes(q) || user.email.toLowerCase().includes(q));
    }
    if (role) items = items.filter((user) => user.role === role);
    if (status) items = items.filter((user) => user.status === status);
    return items;
  }

  findOne(id: number) {
    const user = this.users.find((item) => item.id === id);
    if (!user) throw new NotFoundException('User not found');
    return user;
  }

  create(dto: CreateUserDto) {
    const user: User = {
      id: this.nextId++,
      fullName: dto.fullName,
      email: dto.email,
      phone: dto.phone,
      role: dto.role,
      status: dto.status ?? 'ACTIVE',
      avatar: dto.avatar,
      createdAt: new Date().toISOString(),
    };
    this.users.unshift(user);
    return user;
  }

  update(id: number, dto: UpdateUserDto) {
    const index = this.users.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('User not found');
    const updated = { ...this.users[index], ...dto };
    this.users[index] = updated;
    return updated;
  }

  remove(id: number) {
    const index = this.users.findIndex((item) => item.id === id);
    if (index === -1) throw new NotFoundException('User not found');
    const [deleted] = this.users.splice(index, 1);
    return deleted;
  }
}
