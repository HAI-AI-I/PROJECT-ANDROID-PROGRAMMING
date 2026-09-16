import { Injectable } from '@nestjs/common';
import { existsSync, mkdirSync, readFileSync, renameSync, writeFileSync } from 'node:fs';
import { dirname, join, resolve } from 'node:path';

type DatabaseState = Record<string, unknown[]>;

@Injectable()
export class PersistentStoreService {
  private readonly filePath = process.env.LIBRARY_DATA_FILE
    ? resolve(process.cwd(), process.env.LIBRARY_DATA_FILE)
    : join(process.cwd(), 'data', 'library-data.json');

  private state: DatabaseState = this.load();

  getCollection<T>(name: string, seed: T[]): T[] {
    const existing = this.state[name];
    if (Array.isArray(existing)) return structuredClone(existing) as T[];

    const initial = structuredClone(seed);
    this.state[name] = initial as unknown[];
    this.flush();
    return initial;
  }

  saveCollection<T>(name: string, items: T[]): void {
    this.state[name] = structuredClone(items) as unknown[];
    this.flush();
  }

  private load(): DatabaseState {
    if (!existsSync(this.filePath)) return {};
    try {
      const parsed = JSON.parse(readFileSync(this.filePath, 'utf8')) as DatabaseState;
      return parsed && typeof parsed === 'object' ? parsed : {};
    } catch {
      return {};
    }
  }

  private flush(): void {
    mkdirSync(dirname(this.filePath), { recursive: true });
    const temporaryPath = `${this.filePath}.tmp`;
    writeFileSync(temporaryPath, JSON.stringify(this.state, null, 2), 'utf8');
    renameSync(temporaryPath, this.filePath);
  }
}
