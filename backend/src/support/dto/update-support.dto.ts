import { PartialType } from '@nestjs/swagger';
import { CreateSupportDto } from './create-support.dto.js';

export class UpdateSupportDto extends PartialType(CreateSupportDto) {}
