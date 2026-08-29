import { Ionicons } from '@expo/vector-icons';
import { router } from 'expo-router';
import { useState } from 'react';
import { KeyboardAvoidingView, Platform, Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { DebtTypeToggle } from '@/components/DebtTypeToggle';
import { DueDateInput } from '@/components/DueDateInput';
import { FormField } from '@/components/FormField';
import { SoftButton } from '@/components/SoftButton';
import { Colors, Fonts, Spacing } from '@/constants/theme';
import { useCurrency, useDebtStore } from '@/store/useDebtStore';
import type { DebtType } from '@/types/debt';

export default function AddDebtScreen() {
  const addDebt = useDebtStore((s) => s.addDebt);
  const currency = useCurrency();

  const [type, setType] = useState<DebtType>('owedToMe');
  const [personName, setPersonName] = useState('');
  const [amount, setAmount] = useState('');
  const [dueDate, setDueDate] = useState<string | null>(null);
  const [note, setNote] = useState('');

  const amountValue = Number(amount.replace(/[^\d.]/g, ''));
  const isValid = personName.trim().length > 0 && amountValue > 0;

  const handleSave = () => {
    if (!isValid) return;
    addDebt({
      type,
      personName: personName.trim(),
      amount: amountValue,
      dueDate,
      note: note.trim(),
    });
    router.back();
  };

  return (
    <SafeAreaView style={styles.safeArea} edges={['top', 'bottom']}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Новый долг</Text>
        <Pressable onPress={() => router.back()} style={styles.closeButton} hitSlop={12}>
          <Ionicons name="close" size={22} color={Colors.text} />
        </Pressable>
      </View>

      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
        <ScrollView contentContainerStyle={styles.content} keyboardShouldPersistTaps="handled">
          <DebtTypeToggle value={type} onChange={setType} />

          <FormField
            label={type === 'owedToMe' ? 'Кто должен' : 'Кому должен'}
            value={personName}
            onChangeText={setPersonName}
            placeholder="Имя и фамилия"
          />

          <FormField
            label="Сумма"
            value={amount}
            onChangeText={setAmount}
            placeholder="0"
            keyboardType="numeric"
            right={<Text style={styles.currencySuffix}>{currency}</Text>}
          />

          <DueDateInput label="Срок" value={dueDate} onChange={setDueDate} />

          <FormField
            label="Заметка"
            value={note}
            onChangeText={setNote}
            placeholder="Например: за что и когда договорились"
            multiline
          />
        </ScrollView>

        <View style={styles.footer}>
          <SoftButton
            label="Сохранить долг"
            variant={type === 'owedToMe' ? 'primary' : 'coral'}
            onPress={handleSave}
            disabled={!isValid}
          />
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  flex: {
    flex: 1,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.sm,
    paddingBottom: Spacing.md,
  },
  headerTitle: {
    fontFamily: Fonts.displayBold,
    fontSize: 20,
    color: Colors.text,
  },
  closeButton: {
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  content: {
    paddingHorizontal: Spacing.lg,
    gap: Spacing.lg,
    paddingBottom: Spacing.xl,
  },
  currencySuffix: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 16,
    color: Colors.textMuted,
  },
  footer: {
    paddingHorizontal: Spacing.lg,
    paddingBottom: Spacing.md,
    paddingTop: Spacing.xs,
  },
});
