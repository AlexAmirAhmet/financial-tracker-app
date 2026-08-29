import type { ReactNode } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';

import { Colors, Fonts, Radii, Shadows } from '@/constants/theme';

type ButtonVariant = 'primary' | 'coral' | 'ghost' | 'danger';

interface SoftButtonProps {
  label: string;
  onPress: () => void;
  variant?: ButtonVariant;
  icon?: ReactNode;
  disabled?: boolean;
  loading?: boolean;
}

const VARIANT_BG: Record<ButtonVariant, string> = {
  primary: Colors.teal,
  coral: Colors.coral,
  ghost: Colors.surfaceRaised,
  danger: Colors.coralSoft,
};

const VARIANT_TEXT: Record<ButtonVariant, string> = {
  primary: Colors.white,
  coral: Colors.white,
  ghost: Colors.text,
  danger: Colors.danger,
};

export function SoftButton({ label, onPress, variant = 'primary', icon, disabled, loading }: SoftButtonProps) {
  const isFlat = variant === 'ghost' || variant === 'danger';

  return (
    <Pressable
      onPress={onPress}
      disabled={disabled || loading}
      style={({ pressed }) => [
        styles.base,
        { backgroundColor: VARIANT_BG[variant] },
        !isFlat && Shadows.raisedSm,
        (disabled || loading) && styles.disabled,
        pressed && !disabled && styles.pressed,
      ]}>
      <View style={styles.row}>
        {loading ? (
          <ActivityIndicator color={VARIANT_TEXT[variant]} />
        ) : (
          <>
            {icon}
            <Text style={[styles.label, { color: VARIANT_TEXT[variant] }]}>{label}</Text>
          </>
        )}
      </View>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  base: {
    borderRadius: Radii.md,
    paddingVertical: 16,
    alignItems: 'center',
    justifyContent: 'center',
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  label: {
    fontFamily: Fonts.bodySemiBold,
    fontSize: 16,
  },
  disabled: {
    opacity: 0.5,
  },
  pressed: {
    opacity: 0.9,
    transform: [{ scale: 0.98 }],
  },
});
