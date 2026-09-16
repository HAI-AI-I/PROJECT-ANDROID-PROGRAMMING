import { Injectable, UnauthorizedException } from '@nestjs/common';
import { UsersService } from '../users/users.service.js';
import { LoginDto } from './dto/login.dto.js';

@Injectable()
export class AuthService {
  constructor(private readonly usersService: UsersService) {}

  login(dto: LoginDto) {
    const user = this.usersService.findByIdentifier(dto.identifier);
    const demoPassword = process.env.DEMO_PASSWORD ?? '123456';

    if (!user || dto.password !== demoPassword) {
      throw new UnauthorizedException('Email, số điện thoại hoặc mật khẩu không chính xác');
    }
    if (user.status !== 'ACTIVE') {
      throw new UnauthorizedException('Tài khoản hiện không hoạt động');
    }

    return {
      user: {
        id: user.id,
        fullName: user.fullName,
        email: user.email,
        phone: user.phone,
        role: user.role,
        active: true,
        createdAt: user.createdAt,
      },
    };
  }
}
