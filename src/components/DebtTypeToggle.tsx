import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Colors, Fonts, Radii } from '@/constants/theme';
import type { DebtType } from '@/types/debt';

interface DebtTypeToggleProps {
  value: DebtType;
  onChange: (type: DebtType) => void;
}

const OPTIONS: { key: DebtType; label: string; accent: string; soft: string }[] = [
  { key: 'owedToMe', label: 'Мне должны', accent: Colors.teal, soft: Colors.tealSoft },
  { key: 'iOwe', label: 'Я должен', accent: Colors.coral, soft: Colors.coralSoft },
];

/** Крупный переключатель типа долга для форм добавления. */
export function DebtTypeToggle({ value, onChange }: DebtTypeToggleProps) {
  return (
    <View style={styles.row}>
      {OPTIONS.map((option) => {
        const selected = option.key === value;
        return (
          <Pressable
            key={option.key}
            onPress={() => onChange(option.key)}
            style={[
              styles.option,
              { backgroundColor: selected ? option.soft : Colors.surfaceRaised },
              selected && { borderColor: option.accent },
            ]}>
            <View style={[styles.dot, { backgroundColor: option.accent }]} />
            <Text
              style={[
                styles.label,
                selected && { color: Colors.text, fontFamily: Fonts.bodySemiBold },
              ]}>
              {option.label}
            </Text>
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    gap: 12,
  },
  option: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
    paddingVertical: 14,
    borderRadius: Radii.md,
    borderWidth: 1.5,
    borderColor: 'transparent',
  },
  dot: {
    width: 10,
    height: 10,
    borderRadius: 5,
  },
  label: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 14,
    color: Colors.textSecondary,
  },
});
