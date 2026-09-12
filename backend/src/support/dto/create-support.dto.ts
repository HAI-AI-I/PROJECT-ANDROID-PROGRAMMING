import { IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreateSupportDto {
  @IsNotEmpty()
  userId: number;

  @IsString()
  @IsNotEmpty()
  subject: string;

  @IsString()
  @IsNotEmpty()
  message: string;
}
