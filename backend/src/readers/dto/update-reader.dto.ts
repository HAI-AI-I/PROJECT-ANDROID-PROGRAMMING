import { PartialType } from '@nestjs/swagger';
import { CreateReaderDto } from './create-reader.dto.js';

export class UpdateReaderDto extends PartialType(CreateReaderDto) {}
