import type { ReactNode } from 'react';
import { ScrollView, StyleSheet, type StyleProp, type ViewStyle } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { Colors } from '@/constants/theme';
import { useTabBarInset } from '@/hooks/useTabBarInset';

interface TabScreenProps {
  /** Нескроллящийся контент над списком (заголовок, переключатель, итоги). */
  header?: ReactNode;
  /** Скроллящийся контент экрана. */
  children: ReactNode;
  contentContainerStyle?: StyleProp<ViewStyle>;
}

/**
 * Единая обёртка для экранов внутри (tabs): SafeAreaView сверху + ScrollView,
 * который ВСЕГДА получает нижний отступ под плавающую таб-панель через
 * useTabBarInset(). Используется на всех трёх экранах вкладок (Главная, Долги,
 * Настройки), чтобы отступ под панель физически не мог быть забыт или
 * рассинхронизирован при добавлении нового экрана — он задаётся один раз здесь,
 * а не копипастой в каждом файле.
 */
export function TabScreen({ header, children, contentContainerStyle }: TabScreenProps) {
  const tabBarInset = useTabBarInset();

  return (
    <SafeAreaView style={styles.safeArea} edges={['top']}>
      {header}
      <ScrollView
        showsVerticalScrollIndicator={false}
        contentContainerStyle={[contentContainerStyle, { paddingBottom: tabBarInset }]}>
        {children}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: Colors.background,
  },
});
