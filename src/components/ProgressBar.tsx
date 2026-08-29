import { StyleSheet, View } from 'react-native';

import { Colors, Radii } from '@/constants/theme';
import type { DebtType } from '@/types/debt';

interface ProgressBarProps {
  value: number; // 0..1
  type: DebtType;
  height?: number;
}

export function ProgressBar({ value, type, height = 10 }: ProgressBarProps) {
  const clamped = Math.max(0, Math.min(1, value));
  const fillColor = type === 'owedToMe' ? Colors.teal : Colors.coral;

  return (
    <View style={[styles.track, { height, borderRadius: height / 2 }]}>
      <View
        style={[
          styles.fill,
          {
            width: `${clamped * 100}%`,
            height,
            borderRadius: height / 2,
            backgroundColor: fillColor,
          },
        ]}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  track: {
    width: '100%',
    backgroundColor: Colors.background,
    overflow: 'hidden',
  },
  fill: {},
});
