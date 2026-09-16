export type HistoryAction =
  | "borrow"
  | "return"
  | "extend"
  | "fine"
  | "cancel_request";

export interface HistoryRecord {
  id: string;
  action: HistoryAction;
  userName: string;
  bookTitle: string;
  description: string;
  date: string;
}
