import { Global, Module } from '@nestjs/common';
import { PersistentStoreService } from './persistent-store.service.js';

@Global()
@Module({
  providers: [PersistentStoreService],
  exports: [PersistentStoreService],
})
export class PersistenceModule {}
