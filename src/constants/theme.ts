/**
 * Единая дизайн-система приложения: неоморфизм (soft UI) в бело-голубой палитре.
 * Все цвета, тени, радиусы и типографика собраны здесь, чтобы экраны оставались
 * визуально согласованными.
 */
import { Platform } from 'react-native';

export const Colors = {
  // Фон
  background: '#EAF2F8',
  backgroundTop: '#F6FAFD',
  backgroundBottom: '#DCEAF4',

  // Поверхности (карточки, купола)
  surface: '#EEF4F9',
  surfaceRaised: '#F3F8FB',

  // Текст
  text: '#1E2A33',
  textSecondary: '#6B7C88',
  textMuted: '#95A4AE',

  // Акценты
  teal: '#2FB8C9',
  tealDark: '#1E93A2',
  tealSoft: '#DFF3F6',
  coral: '#FF8A7A',
  coralDark: '#E96B5A',
  coralSoft: '#FFE7E2',

  // Служебные
  white: '#FFFFFF',
  danger: '#E9584A',

  // Тени неоморфизма (rgba, диффузные)
  shadowDark: 'rgba(163, 184, 200, 0.55)',
  shadowLight: 'rgba(255, 255, 255, 0.9)',
} as const;

export const Radii = {
  sm: 12,
  md: 18,
  lg: 24,
  xl: 32,
  pill: 999,
} as const;

export const Spacing = {
  xxs: 4,
  xs: 8,
  sm: 12,
  md: 16,
  lg: 24,
  xl: 32,
  xxl: 40,
} as const;

/**
 * Пресеты мягких теней. iOS честно рисует двойные диффузные тени через shadow*,
 * Android умеет только одну — там имитируем мягкость увеличенным radius и низким opacity.
 */
export const Shadows = {
  raisedSm: Platform.select({
    ios: {
      shadowColor: Colors.shadowDark,
      shadowOffset: { width: 6, height: 6 },
      shadowOpacity: 1,
      shadowRadius: 12,
    },
    android: { elevation: 4 },
    default: {},
  }),
  raisedMd: Platform.select({
    ios: {
      shadowColor: Colors.shadowDark,
      shadowOffset: { width: 10, height: 10 },
      shadowOpacity: 1,
      shadowRadius: 20,
    },
    android: { elevation: 8 },
    default: {},
  }),
  raisedLg: Platform.select({
    ios: {
      shadowColor: Colors.shadowDark,
      shadowOffset: { width: 16, height: 16 },
      shadowOpacity: 1,
      shadowRadius: 32,
    },
    android: { elevation: 14 },
    default: {},
  }),
} as const;

export const Fonts = {
  display: 'Poppins_600SemiBold',
  displayBold: 'Poppins_700Bold',
  displayMedium: 'Poppins_500Medium',
  body: 'Inter_400Regular',
  bodyMedium: 'Inter_500Medium',
  bodySemiBold: 'Inter_600SemiBold',
} as const;

export const BottomTabInset = Platform.select({ ios: 92, android: 88 }) ?? 88;
export const MaxContentWidth = 480;
