import { IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreateBorrowingDto {
  @IsNotEmpty()
  readerId: number;

  @IsNotEmpty()
  bookId: number;

  @IsString()
  @IsNotEmpty()
  borrowDate: string;

  @IsString()
  @IsNotEmpty()
  dueDate: string;

  @IsOptional()
  @IsString()
  note?: string;
}
