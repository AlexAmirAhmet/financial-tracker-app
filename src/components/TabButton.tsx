import { Ionicons } from '@expo/vector-icons';
import { forwardRef } from 'react';
import {
  Pressable,
  StyleSheet,
  Text,
  View,
  type PressableProps,
  type StyleProp,
  type ViewStyle,
} from 'react-native';

import { Colors, Fonts } from '@/constants/theme';

interface TabButtonProps extends Omit<PressableProps, 'style'> {
  icon: keyof typeof Ionicons.glyphMap;
  label: string;
  isFocused?: boolean;
  style?: StyleProp<ViewStyle>;
}

/** Кнопка нижней навигации: иконка + подпись, бирюзовая при активном состоянии. */
export const TabButton = forwardRef<View, TabButtonProps>(function TabButton(
  { icon, label, isFocused, style, ...pressableProps },
  ref,
) {
  const color = isFocused ? Colors.teal : Colors.textMuted;

  return (
    <Pressable ref={ref} style={[styles.button, style]} {...pressableProps}>
      <Ionicons name={icon} size={22} color={color} />
      <Text style={[styles.label, { color }]}>{label}</Text>
    </Pressable>
  );
});

const styles = StyleSheet.create({
  button: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: 4,
    paddingVertical: 8,
  },
  label: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 11,
  },
});
