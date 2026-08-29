import { Ionicons } from '@expo/vector-icons';
import Constants from 'expo-constants';
import { useState } from 'react';
import { Pressable, ScrollView, Share, StyleSheet, Switch, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { FormField } from '@/components/FormField';
import { PersonAvatar } from '@/components/PersonAvatar';
import { SoftCard } from '@/components/SoftCard';
import { BottomTabInset, Colors, Fonts, Radii, Spacing } from '@/constants/theme';
import { useDebtStore } from '@/store/useDebtStore';

const CURRENCIES = ['₽', '$', '€', '₸'];

export default function SettingsScreen() {
  const userName = useDebtStore((s) => s.userName);
  const setUserName = useDebtStore((s) => s.setUserName);
  const currency = useDebtStore((s) => s.currency);
  const setCurrency = useDebtStore((s) => s.setCurrency);
  const notificationsEnabled = useDebtStore((s) => s.notificationsEnabled);
  const setNotificationsEnabled = useDebtStore((s) => s.setNotificationsEnabled);
  const debts = useDebtStore((s) => s.debts);

  const [nameDraft, setNameDraft] = useState(userName);

  const handleExport = async () => {
    const payload = JSON.stringify({ exportedAt: new Date().toISOString(), debts }, null, 2);
    try {
      await Share.share({ message: payload, title: 'Экспорт долгов' });
    } catch {
      // пользователь отменил экспорт — это ожидаемое поведение
    }
  };

  return (
    <SafeAreaView style={styles.safeArea} edges={['top']}>
      <ScrollView
        contentContainerStyle={[styles.content, { paddingBottom: BottomTabInset }]}
        showsVerticalScrollIndicator={false}>
        <Text style={styles.title}>Настройки</Text>

        <SoftCard style={styles.profileCard}>
          <PersonAvatar name={nameDraft || userName} type="owedToMe" size={56} />
          <View style={styles.profileField}>
            <FormField
              label="Ваше имя"
              value={nameDraft}
              onChangeText={setNameDraft}
              onBlur={() => setUserName(nameDraft.trim() || userName)}
              placeholder="Имя"
            />
          </View>
        </SoftCard>

        <Text style={styles.sectionTitle}>ВАЛЮТА</Text>
        <SoftCard style={styles.currencyCard}>
          <View style={styles.currencyRow}>
            {CURRENCIES.map((c) => {
              const selected = c === currency;
              return (
                <Pressable
                  key={c}
                  onPress={() => setCurrency(c)}
                  style={[styles.currencyChip, selected && styles.currencyChipSelected]}>
                  <Text style={[styles.currencyChipText, selected && styles.currencyChipTextSelected]}>
                    {c}
                  </Text>
                </Pressable>
              );
            })}
          </View>
        </SoftCard>

        <Text style={styles.sectionTitle}>УВЕДОМЛЕНИЯ</Text>
        <SoftCard>
          <View style={styles.row}>
            <View style={styles.rowTextWrap}>
              <Text style={styles.rowLabel}>Напоминать о сроках</Text>
              <Text style={styles.rowHint}>Уведомление за день до срока платежа</Text>
            </View>
            <Switch
              value={notificationsEnabled}
              onValueChange={setNotificationsEnabled}
              trackColor={{ false: Colors.background, true: Colors.tealSoft }}
              thumbColor={notificationsEnabled ? Colors.teal : Colors.white}
            />
          </View>
        </SoftCard>

        <Text style={styles.sectionTitle}>ДАННЫЕ</Text>
        <SoftCard onPress={handleExport} style={styles.row}>
          <View style={styles.rowTextWrap}>
            <Text style={styles.rowLabel}>Экспортировать данные</Text>
            <Text style={styles.rowHint}>Поделиться списком долгов в формате JSON</Text>
          </View>
          <Ionicons name="share-outline" size={20} color={Colors.textSecondary} />
        </SoftCard>

        <Text style={styles.sectionTitle}>О ПРИЛОЖЕНИИ</Text>
        <SoftCard style={styles.aboutCard}>
          <Text style={styles.aboutTitle}>Долги</Text>
          <Text style={styles.aboutText}>
            Версия {Constants.expoConfig?.version ?? '1.0.0'}
          </Text>
          <Text style={styles.aboutText}>Все данные хранятся только на вашем устройстве.</Text>
        </SoftCard>
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
  title: {
    fontFamily: Fonts.displayBold,
    fontSize: 24,
    color: Colors.text,
    marginBottom: Spacing.lg,
  },
  profileCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: Spacing.md,
    marginBottom: Spacing.lg,
  },
  profileField: {
    flex: 1,
  },
  sectionTitle: {
    fontFamily: Fonts.bodySemiBold,
    fontSize: 12,
    letterSpacing: 1,
    color: Colors.textMuted,
    marginBottom: Spacing.sm,
    marginTop: Spacing.xs,
  },
  currencyCard: {
    marginBottom: Spacing.lg,
  },
  currencyRow: {
    flexDirection: 'row',
    gap: Spacing.sm,
  },
  currencyChip: {
    width: 48,
    height: 48,
    borderRadius: Radii.md,
    backgroundColor: Colors.background,
    alignItems: 'center',
    justifyContent: 'center',
  },
  currencyChipSelected: {
    backgroundColor: Colors.teal,
  },
  currencyChipText: {
    fontFamily: Fonts.bodySemiBold,
    fontSize: 18,
    color: Colors.textSecondary,
  },
  currencyChipTextSelected: {
    color: Colors.white,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: Spacing.md,
    marginBottom: Spacing.lg,
  },
  rowTextWrap: {
    flex: 1,
    gap: 4,
  },
  rowLabel: {
    fontFamily: Fonts.bodySemiBold,
    fontSize: 15,
    color: Colors.text,
  },
  rowHint: {
    fontFamily: Fonts.body,
    fontSize: 12,
    color: Colors.textMuted,
  },
  aboutCard: {
    gap: 4,
  },
  aboutTitle: {
    fontFamily: Fonts.displayBold,
    fontSize: 16,
    color: Colors.text,
    marginBottom: 4,
  },
  aboutText: {
    fontFamily: Fonts.body,
    fontSize: 13,
    color: Colors.textMuted,
  },
});
