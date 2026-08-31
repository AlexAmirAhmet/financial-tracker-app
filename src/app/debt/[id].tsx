import { Ionicons } from '@expo/vector-icons';
import { router, useLocalSearchParams } from 'expo-router';
import { useMemo } from 'react';
import { Alert, Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { DomeCircle } from '@/components/DomeCircle';
import { ProgressBar } from '@/components/ProgressBar';
import { SoftButton } from '@/components/SoftButton';
import { SoftCard } from '@/components/SoftCard';
import { Colors, Fonts, Spacing } from '@/constants/theme';
import {
  isFullyPaid,
  paidAmount,
  progress,
  remainingAmount,
  useCurrency,
  useDebtStore,
} from '@/store/useDebtStore';
import { formatCurrency, formatDateShort, formatDueIn } from '@/utils/format';

export default function DebtDetailsScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const debts = useDebtStore((s) => s.debts);
  const markClosed = useDebtStore((s) => s.markClosed);
  const deleteDebt = useDebtStore((s) => s.deleteDebt);
  const currency = useCurrency();

  const debt = useMemo(() => debts.find((d) => d.id === id), [debts, id]);

  if (!debt) {
    return (
      <SafeAreaView style={styles.safeArea}>
        <Text style={styles.notFound}>Долг не найден</Text>
      </SafeAreaView>
    );
  }

  const isOwedToMe = debt.type === 'owedToMe';
  const accent = isOwedToMe ? Colors.teal : Colors.coral;
  const remaining = remainingAmount(debt);
  const paid = paidAmount(debt);
  const paidOver100 = progress(debt);
  const closed = isFullyPaid(debt);

  const handleTogglePaid = () => {
    markClosed(debt.id, !debt.closed);
  };

  const handleDelete = () => {
    Alert.alert('Удалить долг?', `Запись о долге «${debt.personName}» будет удалена без возможности восстановления.`, [
      { text: 'Отмена', style: 'cancel' },
      {
        text: 'Удалить',
        style: 'destructive',
        onPress: () => {
          deleteDebt(debt.id);
          router.back();
        },
      },
    ]);
  };

  return (
    <SafeAreaView style={styles.safeArea} edges={['top']}>
      <View style={styles.header}>
        <Pressable onPress={() => router.back()} style={styles.headerButton} hitSlop={12}>
          <Ionicons name="chevron-back" size={24} color={Colors.text} />
        </Pressable>
        <Text style={styles.headerTitle} numberOfLines={1}>
          {debt.personName}
        </Text>
        <Pressable onPress={handleDelete} style={styles.headerButton} hitSlop={12}>
          <Ionicons name="trash-outline" size={20} color={Colors.danger} />
        </Pressable>
      </View>

      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <View style={styles.circleWrap}>
          <DomeCircle screenWidthRatio={0.44} tint={isOwedToMe ? 'teal' : 'coral'}>
            <Text style={styles.circleLabel}>{closed ? 'ОПЛАЧЕНО' : 'ОСТАТОК'}</Text>
            <Text style={[styles.circleValue, { color: accent }]}>{formatCurrency(remaining, currency)}</Text>
            <Text style={styles.circleSub}>из {formatCurrency(debt.amount, currency)}</Text>
          </DomeCircle>
        </View>

        <SoftCard style={styles.progressCard}>
          <View style={styles.progressHeaderRow}>
            <Text style={styles.progressLabel}>Прогресс оплаты</Text>
            <Text style={[styles.progressPercent, { color: accent }]}>
              {Math.round(paidOver100 * 100)}%
            </Text>
          </View>
          <ProgressBar value={paidOver100} type={debt.type} />
          <View style={styles.progressFooterRow}>
            <Text style={styles.progressFooterText}>Оплачено: {formatCurrency(paid, currency)}</Text>
            {debt.dueDate && (
              <Text style={styles.progressFooterText}>
                {formatDateShort(debt.dueDate)} · {formatDueIn(debt.dueDate)}
              </Text>
            )}
          </View>
        </SoftCard>

        {debt.note ? (
          <SoftCard style={styles.noteCard}>
            <Text style={styles.noteLabel}>Заметка</Text>
            <Text style={styles.noteText}>{debt.note}</Text>
          </SoftCard>
        ) : null}

        <Text style={styles.sectionTitle}>ИСТОРИЯ ПЛАТЕЖЕЙ</Text>
        {debt.payments.length === 0 ? (
          <SoftCard>
            <Text style={styles.emptyText}>Платежей пока не было</Text>
          </SoftCard>
        ) : (
          [...debt.payments]
            .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
            .map((payment) => (
              <SoftCard key={payment.id} style={styles.paymentRow}>
                <View style={styles.paymentRowInner}>
                  <Text style={styles.paymentDate}>{formatDateShort(payment.date)}</Text>
                  <Text style={[styles.paymentAmount, { color: accent }]}>
                    {formatCurrency(payment.amount, currency)}
                  </Text>
                </View>
              </SoftCard>
            ))
        )}

        <View style={styles.actions}>
          {!closed && (
            <SoftButton
              label="Добавить платёж"
              variant={isOwedToMe ? 'primary' : 'coral'}
              onPress={() => router.push({ pathname: '/add-payment', params: { debtId: debt.id } })}
            />
          )}
          <SoftButton
            label={closed ? 'Снять отметку об оплате' : 'Отметить оплаченным'}
            variant="ghost"
            onPress={handleTogglePaid}
          />
        </View>
      </ScrollView>
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
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.sm,
    paddingBottom: Spacing.sm,
  },
  headerButton: {
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  headerTitle: {
    flex: 1,
    textAlign: 'center',
    fontFamily: Fonts.bodySemiBold,
    fontSize: 16,
    color: Colors.text,
  },
  content: {
    paddingHorizontal: Spacing.lg,
    paddingBottom: Spacing.xxl,
  },
  circleWrap: {
    alignItems: 'center',
    marginVertical: Spacing.sm,
  },
  circleLabel: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 11,
    letterSpacing: 1,
    color: Colors.textMuted,
    marginBottom: 6,
  },
  circleValue: {
    fontFamily: Fonts.displayBold,
    fontSize: 22,
    textAlign: 'center',
  },
  circleSub: {
    fontFamily: Fonts.body,
    fontSize: 12,
    color: Colors.textMuted,
    marginTop: 4,
  },
  progressCard: {
    gap: 10,
    marginBottom: Spacing.md,
  },
  progressHeaderRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  progressLabel: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 14,
    color: Colors.textSecondary,
  },
  progressPercent: {
    fontFamily: Fonts.displayBold,
    fontSize: 14,
  },
  progressFooterRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  progressFooterText: {
    fontFamily: Fonts.body,
    fontSize: 12,
    color: Colors.textMuted,
  },
  noteCard: {
    gap: 6,
    marginBottom: Spacing.md,
  },
  noteLabel: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 12,
    color: Colors.textMuted,
  },
  noteText: {
    fontFamily: Fonts.body,
    fontSize: 14,
    color: Colors.text,
    lineHeight: 20,
  },
  sectionTitle: {
    fontFamily: Fonts.bodySemiBold,
    fontSize: 12,
    letterSpacing: 1,
    color: Colors.textMuted,
    marginBottom: Spacing.sm,
  },
  emptyText: {
    fontFamily: Fonts.body,
    fontSize: 14,
    color: Colors.textMuted,
    textAlign: 'center',
  },
  paymentRow: {
    marginBottom: 10,
    paddingVertical: 12,
  },
  paymentRowInner: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  paymentDate: {
    fontFamily: Fonts.body,
    fontSize: 14,
    color: Colors.textSecondary,
  },
  paymentAmount: {
    fontFamily: Fonts.bodySemiBold,
    fontSize: 15,
  },
  actions: {
    gap: 12,
    marginTop: Spacing.lg,
  },
});
