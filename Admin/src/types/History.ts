export type HistoryAction =
  | "request"
  | "borrow"
  | "return"
  | "cancel_request";

export interface HistoryRecord {
  id: string;
  action: HistoryAction;
  userName: string;
  bookTitle: string;
  referenceCode: string;
  description: string;
  date: string;
}
