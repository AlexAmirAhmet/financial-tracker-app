import AsyncStorage from '@react-native-async-storage/async-storage';
import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

import type { Debt, DebtType, Payment } from '@/types/debt';

function generateId(): string {
  return `${Date.now().toString(36)}${Math.random().toString(36).slice(2, 8)}`;
}

interface DebtStore {
  debts: Debt[];
  hydrated: boolean;
  userName: string;
  currency: string;
  notificationsEnabled: boolean;

  addDebt: (input: {
    type: DebtType;
    personName: string;
    amount: number;
    dueDate: string | null;
    note: string;
  }) => string;
  addPayment: (debtId: string, amount: number, date: string) => void;
  markClosed: (debtId: string, closed: boolean) => void;
  deleteDebt: (debtId: string) => void;
  setUserName: (name: string) => void;
  setCurrency: (currency: string) => void;
  setNotificationsEnabled: (enabled: boolean) => void;
}

export const useDebtStore = create<DebtStore>()(
  persist(
    (set, get) => ({
      debts: [],
      hydrated: false,
      userName: 'Александр',
      currency: '₽',
      notificationsEnabled: true,

      addDebt: ({ type, personName, amount, dueDate, note }) => {
        const id = generateId();
        const debt: Debt = {
          id,
          type,
          personName,
          amount,
          dueDate,
          note,
          createdAt: new Date().toISOString(),
          payments: [],
          closed: false,
        };
        set({ debts: [debt, ...get().debts] });
        return id;
      },

      addPayment: (debtId, amount, date) => {
        const payment: Payment = { id: generateId(), amount, date };
        set({
          debts: get().debts.map((d) => {
            if (d.id !== debtId) return d;
            const payments = [...d.payments, payment];
            const paid = payments.reduce((sum, p) => sum + p.amount, 0);
            return { ...d, payments, closed: paid >= d.amount };
          }),
        });
      },

      markClosed: (debtId, closed) => {
        set({
          debts: get().debts.map((d) => (d.id === debtId ? { ...d, closed } : d)),
        });
      },

      deleteDebt: (debtId) => {
        set({ debts: get().debts.filter((d) => d.id !== debtId) });
      },

      setUserName: (userName) => set({ userName }),
      setCurrency: (currency) => set({ currency }),
      setNotificationsEnabled: (notificationsEnabled) => set({ notificationsEnabled }),
    }),
    {
      name: 'debt-tracker-storage',
      storage: createJSONStorage(() => AsyncStorage),
      onRehydrateStorage: () => (state) => {
        if (state) state.hydrated = true;
      },
      partialize: (state) => ({
        debts: state.debts,
        userName: state.userName,
        currency: state.currency,
        notificationsEnabled: state.notificationsEnabled,
      }),
    },
  ),
);

export function useCurrency(): string {
  return useDebtStore((s) => s.currency);
}

// --- Вычисляемые селекторы ---

export function paidAmount(debt: Debt): number {
  return debt.payments.reduce((sum, p) => sum + p.amount, 0);
}

/** Долг, отмеченный оплаченным вручную, всегда считается закрытым — даже если
 * сумма платежей в истории меньше суммы долга (например, был прощён или
 * закрыт наличными без записи платежа). */
export function remainingAmount(debt: Debt): number {
  if (debt.closed) return 0;
  return Math.max(0, debt.amount - paidAmount(debt));
}

export function progress(debt: Debt): number {
  if (debt.closed || debt.amount <= 0) return 1;
  return Math.min(1, paidAmount(debt) / debt.amount);
}

export function isFullyPaid(debt: Debt): boolean {
  return debt.closed || remainingAmount(debt) <= 0;
}

export function selectActiveDebts(debts: Debt[], type?: DebtType): Debt[] {
  return debts.filter((d) => !isFullyPaid(d) && (type ? d.type === type : true));
}

export function totalRemaining(debts: Debt[], type: DebtType): number {
  return debts
    .filter((d) => d.type === type && !isFullyPaid(d))
    .reduce((sum, d) => sum + remainingAmount(d), 0);
}

export function balance(debts: Debt[]): number {
  return totalRemaining(debts, 'owedToMe') - totalRemaining(debts, 'iOwe');
}

export function upcomingDebts(debts: Debt[], limit = 5): Debt[] {
  return debts
    .filter((d) => !isFullyPaid(d) && d.dueDate)
    .sort((a, b) => new Date(a.dueDate!).getTime() - new Date(b.dueDate!).getTime())
    .slice(0, limit);
}
