import { Pressable, StyleSheet, Text, View } from 'react-native';

import { Colors, Fonts, Radii, Shadows } from '@/constants/theme';

interface Segment {
  key: string;
  label: string;
  accent?: string;
}

interface SegmentedControlProps {
  segments: Segment[];
  selectedKey: string;
  onChange: (key: string) => void;
}

/** Переключатель на два (или более) сегмента в стиле утопленной неоморфной панели. */
export function SegmentedControl({ segments, selectedKey, onChange }: SegmentedControlProps) {
  return (
    <View style={styles.track}>
      {segments.map((segment) => {
        const selected = segment.key === selectedKey;
        return (
          <Pressable
            key={segment.key}
            onPress={() => onChange(segment.key)}
            style={[
              styles.segment,
              selected && styles.segmentSelected,
              selected && Shadows.raisedSm,
            ]}>
            <View style={styles.labelRow}>
              {segment.accent && (
                <View style={[styles.dot, { backgroundColor: segment.accent }]} />
              )}
              <Text
                style={[
                  styles.label,
                  selected && { color: Colors.text, fontFamily: Fonts.bodySemiBold },
                ]}>
                {segment.label}
              </Text>
            </View>
          </Pressable>
        );
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  track: {
    flexDirection: 'row',
    backgroundColor: Colors.background,
    borderRadius: Radii.md,
    padding: 4,
    gap: 4,
  },
  segment: {
    flex: 1,
    paddingVertical: 10,
    borderRadius: Radii.md - 4,
    alignItems: 'center',
  },
  segmentSelected: {
    backgroundColor: Colors.surfaceRaised,
  },
  labelRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  label: {
    fontFamily: Fonts.bodyMedium,
    fontSize: 14,
    color: Colors.textSecondary,
  },
});
