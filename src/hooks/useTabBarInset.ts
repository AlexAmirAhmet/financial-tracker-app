import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { Spacing, TabBar } from '@/constants/theme';

/**
 * Отступ снизу для прокручиваемого контента на экранах внутри (tabs), чтобы
 * последний элемент списка не перекрывался плавающей нижней панелью. Учитывает
 * реальный safe-area инсет устройства, а не фиксированное значение на платформу.
 */
export function useTabBarInset(extra: number = Spacing.md): number {
  const insets = useSafeAreaInsets();
  return insets.bottom + TabBar.bottomGap + TabBar.contentHeight + extra;
}
