import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { useEffect, useState } from 'react';
import { Pressable, StyleSheet, Text, TextInput, View } from 'react-native';

import { Colors, Fonts, Radii } from '@/constants/theme';

dayjs.extend(customParseFormat);

interface DueDateInputProps {
  label: string;
  value: string | null; // ISO 8601
  onChange: (iso: string | null) => void;
}

const PRESETS = [
  { label: 'Сегодня', days: 0 },
  { label: 'Через неделю', days: 7 },
  { label: 'Через месяц', days: 30 },
];

function toParts(iso: string | null) {
  if (!iso) return { day: '', month: '', year: '' };
  const d = dayjs(iso);
  return { day: d.format('DD'), month: d.format('MM'), year: d.format('YYYY') };
}

/** Простой ввод даты срока: пресеты + ручной ввод ДД / ММ / ГГГГ, без нативного пикера. */
export function DueDateInput({ label, value, onChange }: DueDateInputProps) {
  const [parts, setParts] = useState(() => toParts(value));

  // Подхватываем внешние изменения (например, нажатие пресета).
  useEffect(() => {
    setParts(toParts(value));
  }, [value]);

  const updatePart = (part: 'day' | 'month' | 'year', text: string) => {
    const digits = text.replace(/\D/g, '');
    const next = { ...parts, [part]: digits };
    setParts(next);

    if (!next.day && !next.month && !next.year) {
      onChange(null);
      return;
    }

    if (next.day.length === 2 && next.month.length === 2 && next.year.length === 4) {
      const candidate = dayjs(`${next.year}-${next.month}-${next.day}`, 'YYYY-MM-DD', true);
      if (candidate.isValid()) {
        onChange(candidate.toISOString());
      }
    }
  };

  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>

      <View style={styles.presetsRow}>
        {PRESETS.map((preset) => (
          <Pressable
            key={preset.label}
            style={styles.presetChip}
            onPress={() => onChange(dayjs().add(preset.days, 'day').toISOString())}>
            <Text style={styles.presetText}>{preset.label}</Text>
          </Pressable>
        ))}
        <Pressable style={styles.presetChip} onPress={() => onChange(null)}>
          <Text style={styles.presetText}>Без срока</Text>
        </Pressable>
      </View>

      <View style={styles.dateRow}>
        <TextInput
          value={parts.day}
          onChangeText={(t) => updatePart('day', t)}
          placeholder="ДД"
          placeholderTextColor={Colors.textMuted}
          keyboardType="number-pad"
          maxLength={2}
          style={styles.dateInput}
        />
        <Text style={styles.separator}>.</Text>
        <TextInput
          value={parts.month}
          onChangeText={(t) => updatePart('month', t)}
          placeholder="ММ"
          placeholderTextColor={Colors.textMuted}
          keyboardType="number-pad"
          maxLength={2}
          style={styles.dateInput}
        />
        <Text style={styles.separator}>.</Text>
        <TextInput
          value={parts.year}
          onChangeText={(t) => updatePart('year', t)}
          placeholder="ГГГГ"
          placeholderTextColor={Colors.textMuted}
          keyboardType="number-pad"
          maxLength={4}
          style={[styles.dateInput, styles.yearInput]}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    gap: 8,
  },
  label: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 13,
    color: Colors.textSecondary,
  },
  presetsRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  presetChip: {
    paddingHorizontal: 12,
    paddingVertical: 8,
    borderRadius: Radii.pill,
    backgroundColor: Colors.surfaceRaised,
  },
  presetText: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 12,
    color: Colors.textSecondary,
  },
  dateRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 4,
  },
  dateInput: {
    width: 52,
    height: 48,
    borderRadius: Radii.md,
    backgroundColor: Colors.background,
    borderWidth: 1,
    borderColor: 'rgba(163, 184, 200, 0.25)',
    textAlign: 'center',
    fontFamily: Fonts.bodyMedium,
    fontSize: 16,
    color: Colors.text,
  },
  yearInput: {
    width: 72,
  },
  separator: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 16,
    color: Colors.textMuted,
  },
});
