import { useMemo, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';

import { DebtListItem } from '@/components/DebtListItem';
import { SegmentedControl } from '@/components/SegmentedControl';
import { SoftCard } from '@/components/SoftCard';
import { TabScreen } from '@/components/TabScreen';
import { Colors, Fonts, Spacing } from '@/constants/theme';
import { totalRemaining, useCurrency, useDebtStore } from '@/store/useDebtStore';
import type { DebtType } from '@/types/debt';
import { formatCurrency } from '@/utils/format';

export default function DebtsScreen() {
  const debts = useDebtStore((s) => s.debts);
  const currency = useCurrency();
  const [type, setType] = useState<DebtType>('owedToMe');

  const filtered = useMemo(
    () =>
      [...debts]
        .filter((d) => d.type === type)
        .sort((a, b) => {
          if (a.closed !== b.closed) return a.closed ? 1 : -1;
          return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        }),
    [debts, type],
  );

  const total = totalRemaining(debts, type);

  return (
    <TabScreen
      contentContainerStyle={styles.list}
      header={
        <View>
          <View style={styles.header}>
            <Text style={styles.title}>Долги</Text>
          </View>

          <View style={styles.controlWrap}>
            <SegmentedControl
              segments={[
                { key: 'owedToMe', label: 'Мне должны', accent: Colors.teal },
                { key: 'iOwe', label: 'Я должен', accent: Colors.coral },
              ]}
              selectedKey={type}
              onChange={(key) => setType(key as DebtType)}
            />
          </View>

          <View style={styles.totalRow}>
            <Text style={styles.totalLabel}>Всего активных</Text>
            <Text style={[styles.totalValue, { color: type === 'owedToMe' ? Colors.teal : Colors.coral }]}>
              {formatCurrency(total, currency)}
            </Text>
          </View>
        </View>
      }>
      {filtered.length === 0 ? (
        <SoftCard>
          <Text style={styles.emptyText}>
            {type === 'owedToMe' ? 'Никто вам пока не должен' : 'Вы никому не должны'}
          </Text>
        </SoftCard>
      ) : (
        filtered.map((debt) => (
          <DebtListItem key={debt.id} debt={debt} showProgress={debt.payments.length > 0} />
        ))
      )}
    </TabScreen>
  );
}

const styles = StyleSheet.create({
  header: {
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.sm,
  },
  title: {
    fontFamily: Fonts.displayBold,
    fontSize: 24,
    color: Colors.text,
  },
  controlWrap: {
    paddingHorizontal: Spacing.lg,
    marginTop: Spacing.md,
  },
  totalRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: Spacing.lg,
    marginTop: Spacing.lg,
    marginBottom: Spacing.sm,
  },
  totalLabel: {
    fontFamily: Fonts.body,
    fontSize: 13,
    color: Colors.textSecondary,
  },
  totalValue: {
    fontFamily: Fonts.displayBold,
    fontSize: 20,
  },
  list: {
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.xs,
  },
  emptyText: {
    fontFamily: Fonts.body,
    fontSize: 14,
    color: Colors.textMuted,
    textAlign: 'center',
  },
});
