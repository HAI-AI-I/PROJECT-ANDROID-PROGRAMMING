import { IsDateString, IsInt, IsNotEmpty, IsOptional, IsString, Max, Min } from 'class-validator';

export class CreateBorrowingDto {
  @IsNotEmpty()
  readerId: number;

  @IsNotEmpty()
  bookId: number;

  @IsOptional()
  @IsDateString()
  borrowDate?: string;

  @IsOptional()
  @IsInt()
  @Min(1)
  @Max(90)
  loanDays?: number;

  @IsOptional()
  @IsString()
  note?: string;
}
