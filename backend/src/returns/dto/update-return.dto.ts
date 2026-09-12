import { PartialType } from '@nestjs/swagger';
import { CreateReturnDto } from './create-return.dto.js';

export class UpdateReturnDto extends PartialType(CreateReturnDto) {}
