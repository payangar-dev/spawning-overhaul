package com.spawningoverhaul.spawn.rules;

import com.spawningoverhaul.spawn.SpawnContext;

/**
 * Functional interface for custom mob-specific spawn rules.
 * Returns a multiplier that affects the spawn rate for a specific mob type.
 *
 * Return values:
 * - 0.0: Completely prevent spawning
 * - < 1.0: Reduce spawn rate (probabilistic)
 * - 1.0: No modification (use default multipliers)
 * - > 1.0: Increase spawn rate
 */
@FunctionalInterface
public interface MobSpawnRule {
    /**
     * Calculate the spawn multiplier for this mob based on the spawn context.
     *
     * @param context The spawn context containing environment information
     * @return The spawn rate multiplier for this mob (0.0 to prevent, 1.0 for neutral, >1.0 to boost)
     */
    double getSpawnMultiplier(SpawnContext context);
}
