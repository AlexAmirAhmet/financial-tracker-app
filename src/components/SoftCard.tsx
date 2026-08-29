import type { ReactNode } from 'react';
import { Pressable, StyleSheet, View, type StyleProp, type ViewStyle } from 'react-native';

import { Colors, Radii, Shadows } from '@/constants/theme';

interface SoftCardProps {
  children: ReactNode;
  onPress?: () => void;
  style?: StyleProp<ViewStyle>;
  padded?: boolean;
  radius?: number;
}

/** Базовая приподнятая карточка неоморфизма — фон + мягкая диффузная тень. */
export function SoftCard({ children, onPress, style, padded = true, radius = Radii.lg }: SoftCardProps) {
  const cardStyle = [
    styles.card,
    Shadows.raisedSm,
    { borderRadius: radius },
    padded && styles.padded,
    style,
  ];

  if (onPress) {
    return (
      <Pressable onPress={onPress} style={({ pressed }) => [cardStyle, pressed && styles.pressed]}>
        {children}
      </Pressable>
    );
  }

  return <View style={cardStyle}>{children}</View>;
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: Colors.surfaceRaised,
  },
  padded: {
    padding: 16,
  },
  pressed: {
    opacity: 0.9,
    transform: [{ scale: 0.995 }],
  },
});
