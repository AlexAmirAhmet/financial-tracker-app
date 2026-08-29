import dayjs from 'dayjs';
import 'dayjs/locale/ru';

dayjs.locale('ru');

/** Форматирует сумму в рублях с разрядами: 88000 -> "88 000 ₽" */
export function formatCurrency(amount: number, currency = '₽'): string {
  const rounded = Math.round(Math.abs(amount));
  const grouped = rounded.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
  const sign = amount < 0 ? '-' : '';
  return `${sign}${grouped} ${currency}`;
}

/** "10 янв." — ru-локаль dayjs уже включает точку в сокращении месяца, где нужно. */
export function formatDateShort(iso: string): string {
  return dayjs(iso).format('D MMM');
}

/** "через 7 дн." / "сегодня" / "просрочено на 3 дн." — аббревиатура "дн." не склоняется. */
export function formatDueIn(iso: string): string {
  const now = dayjs().startOf('day');
  const due = dayjs(iso).startOf('day');
  const diff = due.diff(now, 'day');

  if (diff === 0) return 'сегодня';
  if (diff > 0) return `через ${diff} дн.`;
  return `просрочено на ${Math.abs(diff)} дн.`;
}

/** Инициалы для аватара: "Ирина Соколова" -> "ИС" */
export function getInitials(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '?';
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
  return (parts[0][0] + parts[1][0]).toUpperCase();
}
