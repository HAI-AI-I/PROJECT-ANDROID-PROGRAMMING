import { Module } from '@nestjs/common';
import { ReadersController } from './readers.controller.js';
import { ReadersService } from './readers.service.js';

@Module({
  controllers: [ReadersController],
  providers: [ReadersService],
  exports: [ReadersService],
})
export class ReadersModule {}
