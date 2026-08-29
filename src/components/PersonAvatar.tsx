import { StyleSheet, Text, View } from 'react-native';

import { Colors, Fonts, Radii } from '@/constants/theme';
import { getInitials } from '@/utils/format';
import type { DebtType } from '@/types/debt';

interface PersonAvatarProps {
  name: string;
  type: DebtType;
  size?: number;
}

/** Круглый аватар-инициалы, цвет зависит от типа долга (бирюзовый / коралловый). */
export function PersonAvatar({ name, type, size = 48 }: PersonAvatarProps) {
  const bg = type === 'owedToMe' ? Colors.teal : Colors.coral;

  return (
    <View
      style={[
        styles.circle,
        { width: size, height: size, borderRadius: Radii.pill, backgroundColor: bg },
      ]}>
      <Text style={[styles.text, { fontSize: size * 0.36 }]}>{getInitials(name)}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  circle: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    color: Colors.white,
    fontFamily: Fonts.bodySemiBold,
  },
});
