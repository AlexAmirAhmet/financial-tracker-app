import { router } from 'expo-router';
import { StyleSheet, Text, View } from 'react-native';

import { PersonAvatar } from '@/components/PersonAvatar';
import { ProgressBar } from '@/components/ProgressBar';
import { SoftCard } from '@/components/SoftCard';
import { Colors, Fonts } from '@/constants/theme';
import { remainingAmount, progress, useCurrency } from '@/store/useDebtStore';
import type { Debt } from '@/types/debt';
import { formatCurrency, formatDateShort, formatDueIn } from '@/utils/format';

interface DebtListItemProps {
  debt: Debt;
  showProgress?: boolean;
}

/** Карточка одного долга: аватар, имя, срок, сумма — используется на Главной и в списке. */
export function DebtListItem({ debt, showProgress }: DebtListItemProps) {
  const isOwedToMe = debt.type === 'owedToMe';
  const amountColor = isOwedToMe ? Colors.teal : Colors.coral;
  const sign = isOwedToMe ? '+' : '-';
  const remaining = remainingAmount(debt);
  const currency = useCurrency();

  return (
    <SoftCard onPress={() => router.push(`/debt/${debt.id}`)} style={styles.card}>
      <View style={styles.row}>
        <PersonAvatar name={debt.personName} type={debt.type} />
        <View style={styles.info}>
          <Text style={styles.name} numberOfLines={1}>
            {debt.personName}
          </Text>
          <Text style={styles.meta}>
            {debt.dueDate ? `${formatDateShort(debt.dueDate)} · ${formatDueIn(debt.dueDate)}` : 'без срока'}
          </Text>
          {showProgress && (
            <View style={styles.progressRow}>
              <ProgressBar value={progress(debt)} type={debt.type} height={6} />
            </View>
          )}
        </View>
        <Text style={[styles.amount, { color: amountColor }]}>
          {sign}
          {formatCurrency(remaining, currency)}
        </Text>
      </View>
    </SoftCard>
  );
}

const styles = StyleSheet.create({
  card: {
    marginBottom: 12,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
  },
  info: {
    flex: 1,
    gap: 4,
  },
  name: {
    fontFamily: Fonts.bodySemiBold,
    fontSize: 15,
    color: Colors.text,
  },
  meta: {
    fontFamily: Fonts.body,
    fontSize: 12,
    color: Colors.textMuted,
  },
  progressRow: {
    marginTop: 4,
  },
  amount: {
    fontFamily: Fonts.displayMedium,
    fontSize: 15,
  },
});
