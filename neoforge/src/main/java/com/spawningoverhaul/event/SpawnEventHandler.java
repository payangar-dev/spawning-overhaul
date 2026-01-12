package com.spawningoverhaul.event;

import com.spawningoverhaul.SpawningOverhaulCommon;
import com.spawningoverhaul.config.SpawningConfig;
import com.spawningoverhaul.spawn.SpawnContext;
import com.spawningoverhaul.spawn.SpawnMultiplierCalculator;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

/**
 * NeoForge event handler for immersive spawning mechanics.
 * Intercepts mob spawn position checks to apply environment-based spawn rules.
 */
public class SpawnEventHandler {

    /**
     * Handles mob spawn position checks and applies immersive spawning rules.
     * Fired after SpawnPlacements.checkSpawnRules has been evaluated.
     */
    @SubscribeEvent
    public void onMobSpawnPositionCheck(MobSpawnEvent.PositionCheck event) {
        // Get config
        SpawningConfig config = SpawningConfig.HANDLER().instance();

        // Check if immersive spawning is enabled
        if (!config.enableImmersiveSpawning) {
            return; // Let vanilla handle it
        }

        // Verify we're on the server side and have a ServerLevel
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Get spawn information from event
        // Note: MobSpawnEvent.PositionCheck provides access to the Mob entity being spawned
        var mob = event.getEntity();
        if (mob == null) {
            return;
        }

        var entityType = mob.getType();
        // Create BlockPos from event coordinates
        var spawnPos = new net.minecraft.core.BlockPos((int)event.getX(), (int)event.getY(), (int)event.getZ());
        var spawnType = event.getSpawnType();

        // Create spawn context for this attempt
        SpawnContext context = new SpawnContext(serverLevel, spawnPos, entityType, spawnType);

        // Calculate spawn multiplier based on environment and mob rules
        double multiplier = SpawnMultiplierCalculator.calculateMultiplier(context);

        // Make probabilistic spawn decision
        boolean shouldAllow = SpawnMultiplierCalculator.shouldAllowSpawn(multiplier, serverLevel.getRandom());

        // If spawn is denied, set result to FAIL
        if (!shouldAllow) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);

            // Debug logging
            if (SpawningOverhaulCommon.getLogger() != null) {
                SpawningOverhaulCommon.getLogger().info(
                    "Denied spawn of {} at {} (multiplier: {}, outside: {}, cave: {}, forest: {})",
                    entityType.getDescription().getString(),
                    spawnPos,
                    String.format("%.2f", multiplier),
                    context.isOutside(),
                    context.isInCave(),
                    String.format("%.0f%%", context.getForestDensity() * 100.0)
                );
            }
        }
        // If allowed, leave result as DEFAULT to let vanilla checks proceed
    }
}
