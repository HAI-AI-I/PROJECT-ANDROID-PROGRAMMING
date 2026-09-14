import { Type } from 'class-transformer';
import { IsInt, IsNotEmpty, IsNumber, IsOptional, IsString, Min } from 'class-validator';

export class CreateBookDto {
  @IsString()
  @IsNotEmpty()
  title: string;

  @IsString()
  @IsNotEmpty()
  author: string;

  @IsString()
  @IsNotEmpty()
  category: string;

  @IsString()
  @IsNotEmpty()
  publisher: string;

  @Type(() => Number)
  @IsNumber()
  @IsInt()
  @Min(1000)
  publishYear: number;

  @Type(() => Number)
  @IsNumber()
  @IsInt()
  @Min(0)
  quantity: number;

  @IsOptional()
  @IsString()
  cover?: string;
}
