import { LinearGradient } from 'expo-linear-gradient';
import type { ReactNode } from 'react';
import {
  Pressable,
  StyleSheet,
  View,
  useWindowDimensions,
  type StyleProp,
  type ViewStyle,
} from 'react-native';

import { Colors, Shadows } from '@/constants/theme';

type DomeTint = 'neutral' | 'teal' | 'coral';

interface DomeCircleProps {
  /** Доля ширины экрана, которую займёт приподнятый внешний слой (ring-outer). */
  screenWidthRatio?: number;
  /** Верхний предел в пикселях — чтобы на планшетах/вебе купол не раздувался. */
  maxSize?: number;
  children?: ReactNode;
  tint?: DomeTint;
  onPress?: () => void;
  style?: StyleProp<ViewStyle>;
}

const TINT_GLOW: Record<DomeTint, string> = {
  neutral: 'rgba(255, 255, 255, 0.9)',
  teal: 'rgba(47, 184, 201, 0.22)',
  coral: 'rgba(255, 138, 122, 0.22)',
};

/**
 * Купольная круглая поверхность в стиле неоморфизма: внешнее радиальное свечение
 * (имитируется двумя затухающими кольцами, т.к. RN не умеет radial-gradient
 * нативно), приподнятый внешний слой и вдавленный внутренний слой с диагональной
 * бликовой подсветкой сверху-слева.
 *
 * Размер считается от ширины экрана (useWindowDimensions), а не в фиксированных
 * пикселях — иначе на реальных телефонах купол либо переполняет экран, либо
 * выглядит непропорционально в зависимости от диагонали устройства. При
 * screenWidthRatio = 0.5 (по умолчанию) внешний слой ≈ 50% ширины экрана,
 * свечение вокруг него ≈ 57–63%, внутренний круг с текстом ≈ 40%.
 */
export function DomeCircle({
  screenWidthRatio = 0.5,
  maxSize = 260,
  children,
  tint = 'neutral',
  onPress,
  style,
}: DomeCircleProps) {
  const { width } = useWindowDimensions();

  const outerSize = Math.min(width * screenWidthRatio, maxSize);
  const haloSize = outerSize * 1.26; // ≈ 63% ширины экрана при outerSize = 50%
  const haloMidSize = outerSize * 1.14; // ≈ 57% ширины экрана при outerSize = 50%
  const innerSize = outerSize * 0.8; // ≈ 40% ширины экрана при outerSize = 50%

  const glowColor = TINT_GLOW[tint];

  const content = (
    <View style={[styles.wrapper, { width: haloSize, height: haloSize }, style]}>
      <View
        pointerEvents="none"
        style={[
          styles.halo,
          {
            width: haloSize,
            height: haloSize,
            borderRadius: haloSize / 2,
            backgroundColor: glowColor,
          },
        ]}
      />
      <View
        pointerEvents="none"
        style={[
          styles.halo,
          {
            width: haloMidSize,
            height: haloMidSize,
            borderRadius: haloMidSize / 2,
            backgroundColor: tint === 'neutral' ? 'rgba(255,255,255,0.95)' : glowColor,
          },
        ]}
      />

      <View
        style={[
          styles.outer,
          Shadows.raisedLg,
          { width: outerSize, height: outerSize, borderRadius: outerSize / 2 },
        ]}>
        <LinearGradient
          pointerEvents="none"
          colors={['rgba(255,255,255,0.85)', 'rgba(255,255,255,0)']}
          start={{ x: 0.2, y: 0 }}
          end={{ x: 0.8, y: 0.9 }}
          style={[styles.sheen, { width: outerSize, height: outerSize, borderRadius: outerSize / 2 }]}
        />

        <View
          style={[
            styles.inner,
            Shadows.raisedSm,
            { width: innerSize, height: innerSize, borderRadius: innerSize / 2 },
          ]}>
          {children}
        </View>
      </View>
    </View>
  );

  if (onPress) {
    return (
      <Pressable onPress={onPress} hitSlop={12} style={({ pressed }) => [pressed && styles.pressed]}>
        {content}
      </Pressable>
    );
  }

  return content;
}

const styles = StyleSheet.create({
  wrapper: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  halo: {
    position: 'absolute',
  },
  outer: {
    backgroundColor: Colors.surfaceRaised,
    alignItems: 'center',
    justifyContent: 'center',
  },
  sheen: {
    position: 'absolute',
    top: 0,
    left: 0,
  },
  inner: {
    backgroundColor: Colors.surface,
    alignItems: 'center',
    justifyContent: 'center',
  },
  pressed: {
    opacity: 0.85,
    transform: [{ scale: 0.98 }],
  },
});
