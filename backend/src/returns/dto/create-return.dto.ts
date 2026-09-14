import { IsNotEmpty, IsOptional, IsString } from 'class-validator';

export class CreateReturnDto {
  @IsNotEmpty()
  borrowingId: number;

  @IsString()
  @IsNotEmpty()
  returnDate: string;

  @IsString()
  @IsNotEmpty()
  condition: 'GOOD' | 'DAMAGED' | 'LOST';

  @IsNotEmpty()
  fine: number;

  @IsOptional()
  @IsString()
  note?: string;
}
