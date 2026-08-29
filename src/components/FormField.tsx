import type { ReactNode } from 'react';
import { StyleSheet, Text, TextInput, View, type KeyboardTypeOptions } from 'react-native';

import { Colors, Fonts, Radii } from '@/constants/theme';

interface FormFieldProps {
  label: string;
  value: string;
  onChangeText?: (text: string) => void;
  onBlur?: () => void;
  placeholder?: string;
  keyboardType?: KeyboardTypeOptions;
  multiline?: boolean;
  right?: ReactNode;
}

/** Утопленное текстовое поле ввода в стиле soft UI, с подписью сверху. */
export function FormField({
  label,
  value,
  onChangeText,
  onBlur,
  placeholder,
  keyboardType,
  multiline,
  right,
}: FormFieldProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>
      <View style={[styles.inputWrap, multiline && styles.inputWrapMultiline]}>
        <TextInput
          value={value}
          onChangeText={onChangeText}
          onBlur={onBlur}
          placeholder={placeholder}
          placeholderTextColor={Colors.textMuted}
          keyboardType={keyboardType}
          multiline={multiline}
          style={[styles.input, multiline && styles.inputMultiline]}
        />
        {right}
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
  inputWrap: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: Colors.background,
    borderRadius: Radii.md,
    paddingHorizontal: 16,
    minHeight: 52,
    borderWidth: 1,
    borderColor: 'rgba(163, 184, 200, 0.25)',
  },
  inputWrapMultiline: {
    minHeight: 96,
    paddingVertical: 12,
    alignItems: 'flex-start',
  },
  input: {
    flex: 1,
    fontFamily: Fonts.body,
    fontSize: 16,
    color: Colors.text,
  },
  inputMultiline: {
    textAlignVertical: 'top',
  },
});
