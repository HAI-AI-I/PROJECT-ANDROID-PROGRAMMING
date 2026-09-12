import { PartialType } from '@nestjs/swagger';
import { CreateBorrowingDto } from './create-borrowing.dto.js';

export class UpdateBorrowingDto extends PartialType(CreateBorrowingDto) {}
