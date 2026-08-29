import { Ionicons } from '@expo/vector-icons';
import { router } from 'expo-router';
import { TabList, TabSlot, TabTrigger, Tabs } from 'expo-router/ui';
import { Pressable, StyleSheet, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

import { TabButton } from '@/components/TabButton';
import { Colors, Radii, Shadows } from '@/constants/theme';

/**
 * Кастомная нижняя навигация в стиле soft UI: три обычных вкладки и
 * центральная кнопка «Добавить», которая не является вкладкой, а открывает
 * модальное окно создания долга.
 */
export default function TabsLayout() {
  const insets = useSafeAreaInsets();

  return (
    <Tabs style={styles.tabsRoot}>
      <TabSlot />
      <TabList style={[styles.tabList, Shadows.raisedMd, { marginBottom: insets.bottom + 12 }]}>
        <TabTrigger name="index" href="/" asChild>
          <TabButton icon="home" label="Главная" />
        </TabTrigger>
        <TabTrigger name="debts" href="/debts" asChild>
          <TabButton icon="reader-outline" label="Долги" />
        </TabTrigger>

        <Pressable style={styles.addButton} onPress={() => router.push('/add-debt')}>
          <View style={styles.addCircle}>
            <Ionicons name="add" size={26} color={Colors.white} />
          </View>
        </Pressable>

        <TabTrigger name="settings" href="/settings" asChild>
          <TabButton icon="settings-outline" label="Настройки" />
        </TabTrigger>
      </TabList>
    </Tabs>
  );
}

const styles = StyleSheet.create({
  tabsRoot: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  tabList: {
    position: 'absolute',
    left: 20,
    right: 20,
    bottom: 0,
    backgroundColor: Colors.surfaceRaised,
    borderRadius: Radii.xl,
    paddingHorizontal: 8,
    paddingVertical: 6,
    alignItems: 'center',
  },
  addButton: {
    alignItems: 'center',
    justifyContent: 'center',
    marginHorizontal: 4,
    marginTop: -22,
  },
  addCircle: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: Colors.teal,
    alignItems: 'center',
    justifyContent: 'center',
    ...(Shadows.raisedMd as object),
  },
});
