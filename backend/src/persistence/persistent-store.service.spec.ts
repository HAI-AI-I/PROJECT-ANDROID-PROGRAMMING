import { mkdtempSync, readFileSync, rmSync } from 'node:fs';
import { join } from 'node:path';
import { tmpdir } from 'node:os';
import { PersistentStoreService } from './persistent-store.service.js';

describe('PersistentStoreService', () => {
  it('reloads saved collections in a new service instance', () => {
    const directory = mkdtempSync(join(tmpdir(), 'library-store-'));
    const previousPath = process.env.LIBRARY_DATA_FILE;
    process.env.LIBRARY_DATA_FILE = join(directory, 'library.json');

    try {
      const first = new PersistentStoreService();
      first.getCollection('books', [{ id: 1, title: 'Seed' }]);
      first.saveCollection('books', [{ id: 2, title: 'Persisted' }]);

      const second = new PersistentStoreService();
      expect(second.getCollection('books', [])).toEqual([
        { id: 2, title: 'Persisted' },
      ]);
      expect(JSON.parse(readFileSync(process.env.LIBRARY_DATA_FILE, 'utf8'))).toBeTruthy();
    } finally {
      if (previousPath === undefined) delete process.env.LIBRARY_DATA_FILE;
      else process.env.LIBRARY_DATA_FILE = previousPath;
      rmSync(directory, { recursive: true, force: true });
    }
  });
});
