import { Ionicons } from '@expo/vector-icons';
import dayjs from 'dayjs';
import { router, useLocalSearchParams } from 'expo-router';
import { useMemo, useState } from 'react';
import { KeyboardAvoidingView, Platform, Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { DueDateInput } from '@/components/DueDateInput';
import { FormField } from '@/components/FormField';
import { SoftButton } from '@/components/SoftButton';
import { Colors, Fonts, Spacing } from '@/constants/theme';
import { remainingAmount, useCurrency, useDebtStore } from '@/store/useDebtStore';
import { formatCurrency } from '@/utils/format';

export default function AddPaymentScreen() {
  const { debtId } = useLocalSearchParams<{ debtId: string }>();
  const debts = useDebtStore((s) => s.debts);
  const addPayment = useDebtStore((s) => s.addPayment);
  const currency = useCurrency();

  const debt = useMemo(() => debts.find((d) => d.id === debtId), [debts, debtId]);

  const [amount, setAmount] = useState('');
  const [date, setDate] = useState<string | null>(dayjs().toISOString());

  if (!debt) {
    return (
      <SafeAreaView style={styles.safeArea}>
        <Text style={styles.notFound}>Долг не найден</Text>
      </SafeAreaView>
    );
  }

  const remaining = remainingAmount(debt);
  const amountValue = Number(amount.replace(/[^\d.]/g, ''));
  const isValid = amountValue > 0 && !!date;
  const accent = debt.type === 'owedToMe' ? Colors.teal : Colors.coral;

  const handleSave = () => {
    if (!isValid || !date) return;
    addPayment(debt.id, amountValue, date);
    router.back();
  };

  return (
    <SafeAreaView style={styles.safeArea} edges={['top', 'bottom']}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Новый платёж</Text>
        <Pressable onPress={() => router.back()} style={styles.closeButton} hitSlop={12}>
          <Ionicons name="close" size={22} color={Colors.text} />
        </Pressable>
      </View>

      <KeyboardAvoidingView style={styles.flex} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
        <ScrollView contentContainerStyle={styles.content} keyboardShouldPersistTaps="handled">
          <Text style={styles.subtitle}>
            {debt.personName} · остаток {formatCurrency(remaining, currency)}
          </Text>

          <FormField
            label="Сумма платежа"
            value={amount}
            onChangeText={setAmount}
            placeholder="0"
            keyboardType="numeric"
            right={<Text style={styles.currencySuffix}>{currency}</Text>}
          />

          <View style={styles.quickRow}>
            <Pressable style={styles.quickChip} onPress={() => setAmount(String(remaining))}>
              <Text style={styles.quickChipText}>Весь остаток</Text>
            </Pressable>
          </View>

          <DueDateInput label="Дата платежа" value={date} onChange={setDate} />
        </ScrollView>

        <View style={styles.footer}>
          <SoftButton
            label="Сохранить платёж"
            onPress={handleSave}
            disabled={!isValid}
            variant={debt.type === 'owedToMe' ? 'primary' : 'coral'}
          />
        </View>
      </KeyboardAvoidingView>
      <View pointerEvents="none" style={[styles.accentBar, { backgroundColor: accent }]} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  notFound: {
    textAlign: 'center',
    marginTop: 40,
    fontFamily: Fonts.body,
    color: Colors.textMuted,
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
  subtitle: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 14,
    color: Colors.textSecondary,
  },
  currencySuffix: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 16,
    color: Colors.textMuted,
  },
  quickRow: {
    flexDirection: 'row',
    marginTop: -Spacing.sm,
  },
  quickChip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 999,
    backgroundColor: Colors.surfaceRaised,
  },
  quickChipText: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 12,
    color: Colors.textSecondary,
  },
  footer: {
    paddingHorizontal: Spacing.lg,
    paddingBottom: Spacing.md,
    paddingTop: Spacing.xs,
  },
  accentBar: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: 3,
  },
});
