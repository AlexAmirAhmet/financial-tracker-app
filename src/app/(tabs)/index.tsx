import { ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { DebtListItem } from '@/components/DebtListItem';
import { DomeCircle } from '@/components/DomeCircle';
import { PersonAvatar } from '@/components/PersonAvatar';
import { SoftCard } from '@/components/SoftCard';
import { Colors, Fonts, Spacing } from '@/constants/theme';
import { useTabBarInset } from '@/hooks/useTabBarInset';
import { balance, totalRemaining, upcomingDebts, useCurrency, useDebtStore } from '@/store/useDebtStore';
import { formatCurrency } from '@/utils/format';

export default function HomeScreen() {
  const debts = useDebtStore((s) => s.debts);
  const userName = useDebtStore((s) => s.userName);
  const currency = useCurrency();

  const currentBalance = balance(debts);
  const owedToMe = totalRemaining(debts, 'owedToMe');
  const iOwe = totalRemaining(debts, 'iOwe');
  const upcoming = upcomingDebts(debts, 6);
  const tabBarInset = useTabBarInset();

  const isPositive = currentBalance >= 0;

  return (
    <SafeAreaView style={styles.safeArea} edges={['top']}>
      <ScrollView
        contentContainerStyle={[styles.content, { paddingBottom: tabBarInset }]}
        showsVerticalScrollIndicator={false}>
        <View style={styles.header}>
          <View>
            <Text style={styles.greeting}>Привет,</Text>
            <Text style={styles.userName}>{userName}</Text>
          </View>
          <PersonAvatar name={userName} type="owedToMe" size={52} />
        </View>

        <View style={styles.balanceWrap}>
          <DomeCircle screenWidthRatio={0.5} tint={isPositive ? 'teal' : 'coral'}>
            <Text style={styles.balanceLabel}>БАЛАНС</Text>
            <Text style={[styles.balanceValue, { color: isPositive ? Colors.teal : Colors.coral }]}>
              {formatCurrency(currentBalance, currency)}
            </Text>
            <Text style={styles.balanceHint}>{isPositive ? 'вам должны больше' : 'вы в минусе'}</Text>
          </DomeCircle>
        </View>

        <View style={styles.summaryRow}>
          <SoftCard style={styles.summaryCard}>
            <View style={styles.summaryHeaderRow}>
              <View style={[styles.dot, { backgroundColor: Colors.teal }]} />
              <Text style={styles.summaryLabel}>Мне должны</Text>
            </View>
            <Text style={[styles.summaryValue, { color: Colors.teal }]}>{formatCurrency(owedToMe, currency)}</Text>
          </SoftCard>

          <SoftCard style={styles.summaryCard}>
            <View style={styles.summaryHeaderRow}>
              <View style={[styles.dot, { backgroundColor: Colors.coral }]} />
              <Text style={styles.summaryLabel}>Я должен</Text>
            </View>
            <Text style={[styles.summaryValue, { color: Colors.coral }]}>{formatCurrency(iOwe, currency)}</Text>
          </SoftCard>
        </View>

        <Text style={styles.sectionTitle}>БЛИЖАЙШИЕ СРОКИ</Text>

        {upcoming.length === 0 ? (
          <SoftCard>
            <Text style={styles.emptyText}>Пока нет долгов с указанным сроком</Text>
          </SoftCard>
        ) : (
          upcoming.map((debt) => <DebtListItem key={debt.id} debt={debt} />)
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  content: {
    paddingHorizontal: Spacing.lg,
    paddingTop: Spacing.sm,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: Spacing.lg,
  },
  greeting: {
    fontFamily: Fonts.body,
    fontSize: 14,
    color: Colors.textSecondary,
  },
  userName: {
    fontFamily: Fonts.displayBold,
    fontSize: 22,
    color: Colors.text,
  },
  balanceWrap: {
    alignItems: 'center',
    marginVertical: Spacing.sm,
  },
  balanceLabel: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 12,
    letterSpacing: 1,
    color: Colors.textMuted,
    marginBottom: 6,
  },
  balanceValue: {
    fontFamily: Fonts.displayBold,
    fontSize: 26,
    textAlign: 'center',
  },
  balanceHint: {
    fontFamily: Fonts.body,
    fontSize: 12,
    color: Colors.textMuted,
    marginTop: 6,
  },
  summaryRow: {
    flexDirection: 'row',
    gap: Spacing.sm,
    marginBottom: Spacing.lg,
  },
  summaryCard: {
    flex: 1,
    gap: 8,
  },
  summaryHeaderRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  summaryLabel: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 13,
    color: Colors.textSecondary,
  },
  summaryValue: {
    fontFamily: Fonts.displayBold,
    fontSize: 18,
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
});
