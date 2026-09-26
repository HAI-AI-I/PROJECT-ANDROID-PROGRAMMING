"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";
import Input from "@/components/ui/Input";
import Select from "@/components/ui/Select";
import Button from "@/components/ui/Button";
import { useToast } from "@/components/ui/Toast";
import { bookService } from "@/services/bookService";
import { resolveApiAssetUrl } from "@/services/apiClient";
import type { BookCategory } from "@/services/bookService";
import type { Book, BookFormData } from "@/types/Book";
import styles from "./BookForm.module.scss";

type BookSourceMode = "api" | "manual";
const GOOGLE_BOOKS_PAGE_SIZE = 10;

interface ExternalBookMatch {
  id: string;
  isbn: string;
  title: string;
  author: string;
  category: string;
  publisher: string;
  publishYear: string;
  cover?: string;
  description: string;
  workKey?: string;
}

interface BookFormProps {
  initialData?: Book;
  mode: "create" | "edit";
}

const emptyForm: BookFormData = {
  isbn: "",
  title: "",
  author: "",
  category: "",
  publisher: "",
  publishYear: "",
  quantity: "",
  description: "",
  borrowFee: "0",
  shelfLocation: "",
};

function validate(form: BookFormData, mode: "create" | "edit"): Partial<Record<keyof BookFormData, string>> {
  const errors: Partial<Record<keyof BookFormData, string>> = {};
  if (form.isbn.trim().length > 20) errors.isbn = "ISBN không được dài quá 20 ký tự";
  if (!form.title.trim()) errors.title = "Vui lòng nhập tên sách";
  if (!form.author.trim()) errors.author = "Vui lòng nhập tác giả";
  if (!form.category) errors.category = "Vui lòng chọn thể loại";
  if (!form.publisher.trim()) errors.publisher = "Vui lòng nhập nhà xuất bản";
  if (!form.publishYear || isNaN(Number(form.publishYear))) {
    errors.publishYear = "Năm xuất bản không hợp lệ";
  } else if (Number(form.publishYear) < 1000 || Number(form.publishYear) > 2100) {
    errors.publishYear = "Năm xuất bản không hợp lệ";
  }
  const minimumQuantity = mode === "create" ? 1 : 0;
  if (!form.quantity || !Number.isInteger(Number(form.quantity)) || Number(form.quantity) < minimumQuantity) {
    errors.quantity = mode === "create" ? "Số lượng phải từ 1 trở lên" : "Số lượng phải từ 0 trở lên";
  }
  if (!form.borrowFee || !Number.isInteger(Number(form.borrowFee)) || Number(form.borrowFee) < 0) {
    errors.borrowFee = "Phí mượn phải là số nguyên không âm";
  }
  if ((form.cover?.trim().length ?? 0) > 1000) errors.cover = "URL ảnh bìa không được dài quá 1000 ký tự";
  if (form.description.length > 5000) errors.description = "Mô tả không được dài quá 5000 ký tự";
  if (form.shelfLocation.length > 100) errors.shelfLocation = "Vị trí kệ không được dài quá 100 ký tự";
  return errors;
}

function normalizeGoogleBook(item: any): ExternalBookMatch | null {
  const volumeInfo = item?.volumeInfo ?? {};
  const title = String(volumeInfo.title ?? "").trim();
  const authors = Array.isArray(volumeInfo.authors) ? volumeInfo.authors : [];
  const author = authors.join(", ").trim() || "Không rõ tác giả";
  const publisher = String(volumeInfo.publisher ?? "").trim() || "Không rõ nhà xuất bản";
  const publishYear = String(volumeInfo.publishedDate ?? "").slice(0, 4).trim();
  const rawCover = volumeInfo.imageLinks?.thumbnail || volumeInfo.imageLinks?.smallThumbnail || undefined;
  const cover = typeof rawCover === "string" ? rawCover.replace(/^http:\/\//i, "https://") : undefined;
  const identifiers = Array.isArray(volumeInfo.industryIdentifiers) ? volumeInfo.industryIdentifiers : [];
  const isbn = String(
    identifiers.find((item: { type?: string }) => item.type === "ISBN_13")?.identifier
      ?? identifiers.find((item: { type?: string }) => item.type === "ISBN_10")?.identifier
      ?? ""
  ).trim();
  const category = Array.isArray(volumeInfo.categories) && volumeInfo.categories.length > 0
    ? volumeInfo.categories[0]
    : "";

  if (!title) return null;

  return {
    id: String(item?.id ?? title),
    isbn,
    title,
    author,
    category,
    publisher,
    publishYear,
    cover,
    description: String(volumeInfo.description ?? "").trim(),
  };
}

function normalizeOpenLibraryBook(item: any): ExternalBookMatch | null {
  const title = String(item?.title ?? "").trim();
  if (!title) return null;

  const authors = Array.isArray(item.author_name) ? item.author_name : [];
  const publishers = Array.isArray(item.publisher) ? item.publisher : [];
  const subjects = Array.isArray(item.subject) ? item.subject : [];
  const isbns = Array.isArray(item.isbn) ? item.isbn.map(String) : [];
  const isbn = isbns.find((value: string) => /^\d{13}$/.test(value))
    ?? isbns.find((value: string) => /^\d{10}$/.test(value))
    ?? "";
  const coverId = Number(item.cover_i);
  const firstSentence = Array.isArray(item.first_sentence)
    ? String(item.first_sentence[0] ?? "").trim()
    : String(item.first_sentence ?? "").trim();
  const workKey = String(item.key ?? "").trim();

  return {
    id: `open-library:${String(item.key ?? title)}:${isbn}`,
    isbn,
    title,
    author: authors.join(", ").trim() || "Không rõ tác giả",
    category: String(subjects[0] ?? "").trim(),
    publisher: String(publishers[0] ?? "").trim() || "Không rõ nhà xuất bản",
    publishYear: item.first_publish_year ? String(item.first_publish_year) : "",
    cover: Number.isFinite(coverId) && coverId > 0
      ? `https://covers.openlibrary.org/b/id/${coverId}-L.jpg`
      : undefined,
    description: firstSentence,
    workKey: workKey.startsWith("/works/") ? workKey : undefined,
  };
}

function normalizeForComparison(value: string): string {
  return value
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLocaleLowerCase("vi-VN")
    .replace(/[^a-z0-9]+/g, " ")
    .trim();
}

function matchCategory(category: string, categories: BookCategory[]): string {
  const normalizedCategory = normalizeForComparison(category.split("/")[0] ?? "");
  if (!normalizedCategory) return "";

  const match = categories.find((item) => {
    const normalizedName = normalizeForComparison(item.name);
    return normalizedName === normalizedCategory
      || normalizedName.includes(normalizedCategory)
      || normalizedCategory.includes(normalizedName);
  });
  return match?.name ?? category.trim();
}

function buildGoogleBooksQuery(value: string): string {
  const compactIsbn = value.replace(/[\s-]/g, "");
  return /^(?:\d{10}|\d{13})$/.test(compactIsbn) ? `isbn:${compactIsbn}` : value;
}

function extractOpenLibraryText(value: unknown): string {
  if (typeof value === "string") return value.trim();
  if (value && typeof value === "object" && "value" in value) {
    const nestedValue = (value as { value?: unknown }).value;
    return typeof nestedValue === "string" ? nestedValue.trim() : "";
  }
  return "";
}

export default function BookForm({ initialData, mode }: BookFormProps) {
  const router = useRouter();
  const { showToast } = useToast();
  const [sourceMode, setSourceMode] = useState<BookSourceMode>(mode === "edit" ? "manual" : "api");
  const [searchTerm, setSearchTerm] = useState("");
  const [apiResults, setApiResults] = useState<ExternalBookMatch[]>([]);
  const [selectedApiId, setSelectedApiId] = useState<string | null>(null);
  const [apiLoading, setApiLoading] = useState(false);
  const [googleSearchError, setGoogleSearchError] = useState<string | null>(null);
  const [nextGoogleStartIndex, setNextGoogleStartIndex] = useState(0);
  const [hasMoreGoogleResults, setHasMoreGoogleResults] = useState(false);
  const [searchProvider, setSearchProvider] = useState<"google" | "open-library">("google");
  const [categories, setCategories] = useState<BookCategory[]>([]);
  const [form, setForm] = useState<BookFormData>(
    initialData
      ? {
          isbn: initialData.isbn ?? "",
          title: initialData.title,
          author: initialData.author,
          category: initialData.category,
          publisher: initialData.publisher ?? "",
          publishYear: initialData.publishYear == null ? "" : String(initialData.publishYear),
          quantity: String(initialData.quantity),
          cover: initialData.cover,
          description: initialData.description ?? "",
          borrowFee: String(initialData.borrowFee ?? 0),
          shelfLocation: initialData.shelfLocation ?? "",
          authorDetails: initialData.authorDetails,
          publisherDetails: initialData.publisherDetails,
        }
      : emptyForm
  );
  const [errors, setErrors] = useState<Partial<Record<keyof BookFormData, string>>>({});
  const [loading, setLoading] = useState(false);
  const [coverFile, setCoverFile] = useState<File | null>(null);
  const [coverPreviewUrl, setCoverPreviewUrl] = useState<string | null>(null);
  const [googleCoverUrl, setGoogleCoverUrl] = useState<string | null>(null);
  const [descriptionLoadingId, setDescriptionLoadingId] = useState<string | null>(null);
  const selectedBookIdRef = useRef<string | null>(null);

  const canUseApiLookup = useMemo(() => sourceMode === "api", [sourceMode]);
  const categoryOptions = useMemo(() => {
    const items = categories.map((item) => ({ value: item.name, label: item.name }));
    if (form.category && !items.some((item) => item.value === form.category)) {
      items.push({ value: form.category, label: form.category });
    }
    return items;
  }, [categories, form.category]);

  useEffect(() => {
    bookService.getCategories()
      .then(setCategories)
      .catch(() => setCategories([]));
  }, []);

  useEffect(() => () => {
    if (coverPreviewUrl) URL.revokeObjectURL(coverPreviewUrl);
  }, [coverPreviewUrl]);

  const update = (field: keyof BookFormData, value: string) => {
    setForm((prev) => ({
      ...prev,
      [field]: value,
      ...(field === "author" ? { authorDetails: undefined } : {}),
      ...(field === "publisher" ? { publisherDetails: undefined } : {}),
    }));
    setErrors((prev) => ({ ...prev, [field]: undefined }));
  };

  const applyGoogleBook = (book: ExternalBookMatch) => {
    selectedBookIdRef.current = book.id;
    setCoverFile(null);
    setCoverPreviewUrl(null);
    setGoogleCoverUrl(book.cover ?? null);
    setForm((prev) => ({
      ...prev,
      isbn: book.isbn || prev.isbn,
      title: book.title || prev.title,
      author: book.author || prev.author,
      category: matchCategory(book.category, categories) || prev.category,
      publisher: book.publisher || prev.publisher,
      publishYear: book.publishYear || prev.publishYear,
      cover: book.cover || prev.cover,
      description: book.description || prev.description,
      authorDetails: undefined,
      publisherDetails: undefined,
    }));
    setSelectedApiId(book.id);
    setErrors((prev) => ({
      ...prev,
      title: undefined,
      isbn: undefined,
      author: undefined,
      category: undefined,
      publisher: undefined,
      publishYear: undefined,
    }));

    if (!book.description && (book.workKey || book.isbn)) {
      void loadOpenLibraryDescription(book);
    }
  };

  const loadOpenLibraryDescription = async (book: ExternalBookMatch) => {
    if (!book.workKey && !book.isbn) return;

    setDescriptionLoadingId(book.id);
    const controller = new AbortController();
    const timeoutId = window.setTimeout(() => controller.abort(), 12_000);
    try {
      let description = "";
      if (book.workKey) {
        const workResponse = await fetch(`https://openlibrary.org${book.workKey}.json`, {
          signal: controller.signal,
        });
        const workData = workResponse.ok ? await workResponse.json() : null;
        description = extractOpenLibraryText(workData?.description);
      }

      if (!description && book.isbn) {
        const editionResponse = await fetch(
          `https://openlibrary.org/isbn/${encodeURIComponent(book.isbn)}.json`,
          { signal: controller.signal }
        );
        if (editionResponse.ok) {
          const editionData = await editionResponse.json();
          description = extractOpenLibraryText(editionData.description)
            || extractOpenLibraryText(editionData.notes);
        }
      }

      if (description && selectedBookIdRef.current === book.id) {
        setForm((current) => ({
          ...current,
          description: current.description.trim() ? current.description : description.slice(0, 5000),
        }));
      }
    } catch {
      // Some Open Library records do not provide a readable work description.
    } finally {
      window.clearTimeout(timeoutId);
      setDescriptionLoadingId((current) => current === book.id ? null : current);
    }
  };

  const handleCoverSelection = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = "";
    if (!file) return;

    const allowedTypes = ["image/jpeg", "image/png", "image/webp"];
    if (!allowedTypes.includes(file.type)) {
      setErrors((prev) => ({ ...prev, cover: "Chỉ hỗ trợ ảnh JPEG, PNG hoặc WebP" }));
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setErrors((prev) => ({ ...prev, cover: "Ảnh bìa không được vượt quá 5 MB" }));
      return;
    }

    setCoverFile(file);
    setCoverPreviewUrl(URL.createObjectURL(file));
    setGoogleCoverUrl(null);
    setErrors((prev) => ({ ...prev, cover: undefined }));
  };

  const removeCover = () => {
    setCoverFile(null);
    setCoverPreviewUrl(null);
    setGoogleCoverUrl(null);
    update("cover", "");
  };

  const updateCoverUrl = (value: string) => {
    setCoverFile(null);
    setCoverPreviewUrl(null);
    setGoogleCoverUrl(null);
    update("cover", value);
  };

  const fetchGoogleBooks = async (startIndex: number, append: boolean) => {
    const query = searchTerm.trim();

    if (!query) {
      showToast("Vui lòng nhập tên sách hoặc ISBN để tra cứu", "error");
      return;
    }

    setApiLoading(true);
    setGoogleSearchError(null);
    const controller = new AbortController();
    const timeoutId = window.setTimeout(() => controller.abort(), 12_000);
    try {
      let matches: ExternalBookMatch[] = [];
      let rawResultCount = 0;
      let totalItems = 0;
      let activeProvider = append ? searchProvider : "google";

      if (activeProvider === "google") {
        const googleQuery = buildGoogleBooksQuery(query);
        const googleUrl = `https://www.googleapis.com/books/v1/volumes?q=${encodeURIComponent(googleQuery)}&startIndex=${startIndex}&maxResults=${GOOGLE_BOOKS_PAGE_SIZE}&printType=books`;
        const googleResponse = await fetch(googleUrl, { signal: controller.signal });

        if (googleResponse.status === 429) {
          activeProvider = "open-library";
          setSearchProvider("open-library");
          if (!append) showToast("Google Books đã hết hạn mức, đang dùng Open Library");
        } else {
          if (!googleResponse.ok) throw new Error(`Google Books trả về lỗi ${googleResponse.status}`);
          const data = await googleResponse.json();
          const items: Array<Record<string, any>> = Array.isArray(data.items) ? data.items : [];
          rawResultCount = items.length;
          totalItems = Number(data.totalItems ?? 0);
          matches = items
            .map((item: Record<string, any>) => normalizeGoogleBook(item))
            .filter((item): item is ExternalBookMatch => Boolean(item));
          setSearchProvider("google");
        }
      }

      if (activeProvider === "open-library") {
        const compactIsbn = query.replace(/[\s-]/g, "");
        const searchParameter = /^(?:\d{10}|\d{13})$/.test(compactIsbn)
          ? `isbn=${encodeURIComponent(compactIsbn)}`
          : `q=${encodeURIComponent(query)}`;
        const openLibraryUrl = `https://openlibrary.org/search.json?${searchParameter}&offset=${startIndex}&limit=${GOOGLE_BOOKS_PAGE_SIZE}&fields=key,title,author_name,isbn,publisher,first_publish_year,cover_i,subject,first_sentence`;
        const openLibraryResponse = await fetch(openLibraryUrl, { signal: controller.signal });
        if (!openLibraryResponse.ok) {
          throw new Error(`Open Library trả về lỗi ${openLibraryResponse.status}`);
        }
        const data = await openLibraryResponse.json();
        const items: Array<Record<string, any>> = Array.isArray(data.docs) ? data.docs : [];
        rawResultCount = items.length;
        totalItems = Number(data.numFound ?? data.num_found ?? 0);
        matches = items
          .map((item: Record<string, any>) => normalizeOpenLibraryBook(item))
          .filter((item): item is ExternalBookMatch => Boolean(item));
        setSearchProvider("open-library");
      }

      if (matches.length === 0) {
        if (!append) setApiResults([]);
        setHasMoreGoogleResults(false);
        setGoogleSearchError(append ? "Không còn kết quả nào khác." : "Không tìm thấy sách phù hợp trên Google Books.");
        return;
      }

      setApiResults((current) => {
        const combined = append ? [...current, ...matches] : matches;
        return combined.filter((item, index) => combined.findIndex((candidate) => candidate.id === item.id) === index);
      });
      setNextGoogleStartIndex(startIndex + rawResultCount);
      setHasMoreGoogleResults(
        rawResultCount === GOOGLE_BOOKS_PAGE_SIZE
        && startIndex + rawResultCount < totalItems
      );
      if (!append) {
        setSelectedApiId(matches[0].id);
        applyGoogleBook(matches[0]);
        showToast(`Đã tải dữ liệu sách từ ${activeProvider === "google" ? "Google Books" : "Open Library"}`);
      }
    } catch (error) {
      if (!append) setApiResults([]);
      const message = error instanceof DOMException && error.name === "AbortError"
        ? "Google Books phản hồi quá lâu. Vui lòng thử lại."
        : error instanceof Error
          ? error.message
          : "Không thể kết nối nguồn dữ liệu sách. Bạn vẫn có thể nhập sách thủ công.";
      setGoogleSearchError(message);
      showToast(message, "error");
    } finally {
      window.clearTimeout(timeoutId);
      setApiLoading(false);
    }
  };

  const handleGoogleSearch = async (event?: React.FormEvent) => {
    event?.preventDefault();
    await fetchGoogleBooks(0, false);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const validationErrors = validate(form, mode);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setLoading(true);
    try {
      const normalizedIsbn = form.isbn.trim();
      if (normalizedIsbn) {
        const isbnAvailable = await bookService.isIsbnAvailable(normalizedIsbn, initialData?.bookId);
        if (!isbnAvailable) {
          setErrors((prev) => ({ ...prev, isbn: "ISBN này đã tồn tại trong hệ thống" }));
          showToast("ISBN này đã được sử dụng cho một cuốn sách khác", "error");
          return;
        }
      }

      let submittedForm = form;
      if (coverFile) {
        const uploadedCover = await bookService.uploadBookCover(coverFile);
        submittedForm = { ...form, cover: uploadedCover.path };
      } else if (googleCoverUrl && form.cover === googleCoverUrl) {
        const importedCover = await bookService.importBookCover(googleCoverUrl);
        submittedForm = { ...form, cover: importedCover.path };
      }

      if (mode === "create") {
        const book = await bookService.createBook(submittedForm);
        showToast("Thêm sách thành công");
        router.push(`/admin/books/${book.bookId}`);
      } else if (initialData) {
        await bookService.updateBook(initialData.bookId, submittedForm);
        showToast("Cập nhật sách thành công");
        router.push(`/admin/books/${initialData.bookId}`);
      }
    } catch (error) {
      showToast(error instanceof Error ? error.message : "Có lỗi xảy ra, vui lòng thử lại", "error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form className={styles.form} onSubmit={handleSubmit}>
      <div className={styles.section}>
        <h2 className={styles.sectionTitle}>Thông tin sách</h2>

        <div className={styles.sourceSwitch}>
          <button
            type="button"
            className={`${styles.sourceButton} ${canUseApiLookup ? styles.active : ""}`}
            onClick={() => setSourceMode("api")}
          >
            Tìm sách tự động
          </button>
          <button
            type="button"
            className={`${styles.sourceButton} ${!canUseApiLookup ? styles.active : ""}`}
            onClick={() => setSourceMode("manual")}
          >
            Nhập tay
          </button>
        </div>

        {sourceMode === "api" && (
          <div className={styles.apiLookupPanel}>
            <div className={styles.searchRow}>
              <Input
                label="Tên sách / ISBN / mã vạch"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                onKeyDown={(event) => {
                  if (event.key === "Enter") {
                    event.preventDefault();
                    void handleGoogleSearch();
                  }
                }}
                placeholder="VD: Dune, 9780141187761"
              />
              <Button
                type="button"
                disabled={apiLoading}
                className={styles.searchButton}
                onClick={() => void handleGoogleSearch()}
              >
                {apiLoading ? "Đang tìm..." : "Tìm sách"}
              </Button>
            </div>

            {apiResults.length > 0 && (
              <div className={styles.resultList}>
                <p className={styles.resultSource}>
                  Kết quả từ {searchProvider === "google" ? "Google Books" : "Open Library"}
                </p>
                {apiResults.map((item) => (
                  <button
                    key={item.id}
                    type="button"
                    className={`${styles.resultItem} ${selectedApiId === item.id ? styles.resultItemSelected : ""}`}
                    onClick={() => applyGoogleBook(item)}
                  >
                    <span className={styles.resultThumb}>
                      {item.cover ? <img src={item.cover} alt={item.title} /> : <span>📚</span>}
                    </span>
                    <span className={styles.resultInfo}>
                      <strong>{item.title}</strong>
                      <small>{item.author}</small>
                      {descriptionLoadingId === item.id && <small>Đang tải mô tả...</small>}
                    </span>
                  </button>
                ))}
              </div>
            )}

            {googleSearchError && <p className={styles.apiError}>{googleSearchError}</p>}

            {hasMoreGoogleResults && (
              <div className={styles.loadMoreRow}>
                <Button
                  type="button"
                  variant="secondary"
                  disabled={apiLoading}
                  onClick={() => void fetchGoogleBooks(nextGoogleStartIndex, true)}
                >
                  {apiLoading ? "Đang tải..." : "Xem thêm kết quả"}
                </Button>
              </div>
            )}
          </div>
        )}

        <div className={styles.grid}>
          <div className={styles.full}>
            <p className={styles.sectionTitle} style={{ fontSize: 14, marginBottom: 8 }}>
              Ảnh bìa
            </p>

            <label className={styles.coverUploadLabel}>
              <input
                type="file"
                accept="image/jpeg,image/png,image/webp"
                onChange={handleCoverSelection}
                disabled={loading}
              />
              <div className={styles.coverPreview}>
                {coverPreviewUrl || form.cover ? (
                  <img src={coverPreviewUrl ?? resolveApiAssetUrl(form.cover)} alt="Bìa sách" />
                ) : (
                  <span>Chưa có ảnh</span>
                )}
                <span className={styles.coverOverlay}>
                  {coverFile ? "Đã chọn ảnh" : "Chọn ảnh"}
                </span>
              </div>
            </label>

            <p className={styles.coverHint}>JPEG, PNG hoặc WebP, tối đa 5 MB.</p>
            {errors.cover && <span className={styles.errorText}>{errors.cover}</span>}

            {(coverPreviewUrl || form.cover) && (
              <div className={styles.coverRemoveRow}>
                <button type="button" className={styles.removeCoverButton} onClick={removeCover}>
                  Xóa ảnh
                </button>
              </div>
            )}
          </div>

          <div className={styles.full}>
            <Input
              label="Tên sách"
              value={form.title}
              onChange={(e) => update("title", e.target.value)}
              error={errors.title}
              placeholder="Nhập tên sách"
            />
          </div>

          <Input
            label="ISBN"
            value={form.isbn}
            onChange={(e) => update("isbn", e.target.value)}
            error={errors.isbn}
            placeholder="VD: 9780132350884"
            maxLength={20}
          />

          <div className={styles.full}>
            <Input
              label="URL ảnh bìa"
              value={form.cover ?? ""}
              onChange={(e) => updateCoverUrl(e.target.value)}
              error={errors.cover}
              placeholder="https://example.com/cover.jpg"
              maxLength={1000}
            />
          </div>

          <Input
            label="Tác giả"
            value={form.author}
            onChange={(e) => update("author", e.target.value)}
            error={errors.author}
            placeholder="Nhập tác giả"
          />

          <Select
            label="Thể loại"
            value={form.category}
            onChange={(e) => update("category", e.target.value)}
            placeholder="Chọn thể loại"
            options={categoryOptions}
          />
          {errors.category && (
            <span style={{ color: "var(--danger)", fontSize: 12, gridColumn: "1 / -1", marginTop: -8 }}>
              {errors.category}
            </span>
          )}

          <Input
            label="Nhà xuất bản"
            value={form.publisher}
            onChange={(e) => update("publisher", e.target.value)}
            error={errors.publisher}
            placeholder="Nhập nhà xuất bản"
          />

          <Input
            label="Năm xuất bản"
            type="number"
            value={form.publishYear}
            onChange={(e) => update("publishYear", e.target.value)}
            error={errors.publishYear}
            placeholder="2024"
          />

          <Input
            label="Số lượng"
            type="number"
            value={form.quantity}
            onChange={(e) => update("quantity", e.target.value)}
            error={errors.quantity}
            placeholder={mode === "create" ? "1" : "0"}
            min={mode === "create" ? 1 : 0}
            step={1}
          />

          <Input
            label="Phí mượn (VNĐ)"
            type="number"
            value={form.borrowFee}
            onChange={(e) => update("borrowFee", e.target.value)}
            error={errors.borrowFee}
            min={0}
            step={1}
          />

          <Input
            label="Vị trí kệ"
            value={form.shelfLocation}
            onChange={(e) => update("shelfLocation", e.target.value)}
            error={errors.shelfLocation}
            placeholder="VD: Kệ A-03"
            maxLength={100}
          />

          <div className={styles.full}>
            <label className={styles.textareaLabel} htmlFor="book-description">Mô tả</label>
            <textarea
              id="book-description"
              className={`${styles.textarea} ${errors.description ? styles.textareaError : ""}`}
              value={form.description}
              onChange={(event) => update("description", event.target.value)}
              placeholder="Nhập mô tả nội dung sách"
              maxLength={5000}
              rows={5}
            />
            {errors.description && <span className={styles.errorText}>{errors.description}</span>}
          </div>
        </div>

        <div className={styles.actions}>
          <Button type="button" variant="secondary" onClick={() => router.back()}>
            Hủy
          </Button>
          <Button type="submit" disabled={loading}>
            {loading ? (coverFile ? "Đang tải ảnh và lưu..." : "Đang lưu...") : "Lưu sách"}
          </Button>
        </div>
      </div>
    </form>
  );
}
