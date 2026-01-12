package com.spawningoverhaul.mixin;

import com.spawningoverhaul.config.SpawningConfig;
import com.spawningoverhaul.spawn.SpawnContext;
import com.spawningoverhaul.spawn.SpawnMultiplierCalculator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.NaturalSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public class SpawnHelperMixin {

    /**
     * Inject into the spawn position validation to modify spawn decisions based on immersive spawning rules.
     * This intercepts natural spawn attempts and applies environmental multipliers.
     */
    @Inject(method = "isSpawnPositionOk", at = @At("HEAD"), cancellable = true)
    private static void onIsSpawnPositionOk(
            SpawnPlacementTypes placementType,
            ServerLevel level,
            BlockPos pos,
            EntityType<?> entityType,
            CallbackInfoReturnable<Boolean> cir
    ) {
        SpawningConfig config = SpawningConfig.HANDLER().instance();

        // Check if immersive spawning is enabled
        if (!config.enableImmersiveSpawning) {
            return; // Let vanilla handle it
        }

        // Create spawn context for this attempt
        SpawnContext context = new SpawnContext(level, pos, entityType, MobSpawnType.NATURAL);

        // Calculate spawn multiplier based on environment and mob rules
        double multiplier = SpawnMultiplierCalculator.calculateMultiplier(context);

        // Make probabilistic spawn decision
        RandomSource random = level.getRandom();
        boolean shouldAllow = SpawnMultiplierCalculator.shouldAllowSpawn(multiplier, random);

        // If spawn is denied, cancel the spawn
        if (!shouldAllow) {
            cir.setReturnValue(false);
        }
        // If allowed, let vanilla checks proceed (don't set return value)
    }
}
