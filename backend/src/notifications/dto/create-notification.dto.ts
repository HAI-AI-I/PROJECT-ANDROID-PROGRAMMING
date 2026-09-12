import { IsBoolean, IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreateNotificationDto {
  @IsNotEmpty()
  userId: number;

  @IsString()
  @IsNotEmpty()
  title: string;

  @IsString()
  @IsNotEmpty()
  message: string;

  @IsString()
  @IsNotEmpty()
  type: 'BORROW' | 'RETURN' | 'OVERDUE' | 'SYSTEM' | 'SUPPORT';

  @IsOptional()
  @IsBoolean()
  isRead?: boolean;
}
