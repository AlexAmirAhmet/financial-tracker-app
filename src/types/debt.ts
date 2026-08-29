/** Тип долга: кому я должен, или кто должен мне. */
export type DebtType = 'owedToMe' | 'iOwe';

export interface Payment {
  id: string;
  amount: number;
  date: string; // ISO 8601
}

export interface Debt {
  id: string;
  type: DebtType;
  personName: string;
  amount: number; // исходная сумма долга
  dueDate: string | null; // ISO 8601
  note: string;
  createdAt: string; // ISO 8601
  payments: Payment[];
  closed: boolean; // отмечено как полностью оплаченное
}
