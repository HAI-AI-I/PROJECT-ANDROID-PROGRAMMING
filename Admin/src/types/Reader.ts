export type ReaderStatus = "active" | "inactive" | "suspended";

export interface Reader {
  readerId: string;
  name: string;
  email: string;
  phone: string;
  avatar?: string;
  booksBorrowing: number;
  status: ReaderStatus;
  registeredDate: string;
}

export interface ReaderFormData {
  name: string;
  email: string;
  phone: string;
  status: ReaderStatus;
}
