package com.spawningoverhaul.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;

/**
 * Immutable snapshot of a spawn attempt with lazy-evaluated environment detection.
 * Caches expensive detection results to avoid redundant world queries.
 */
public class SpawnContext {
    private final Level level;
    private final BlockPos spawnPos;
    private final EntityType<?> entityType;
    private final MobSpawnType spawnType;

    // Lazy-evaluated cached results (Boolean/Double vs primitive for null-checking)
    private Boolean isNight;
    private Boolean isOutside;
    private Double forestDensity;
    private Boolean isInCave;
    private Double caveDepth;
    private Boolean isInDangerousStructure;

    public SpawnContext(Level level, BlockPos spawnPos, EntityType<?> entityType, MobSpawnType spawnType) {
        this.level = level;
        this.spawnPos = spawnPos;
        this.entityType = entityType;
        this.spawnType = spawnType;
    }

    /**
     * Pre-populate all environment checks in one pass.
     * Useful when you know you'll need all the data.
     */
    public void detectEnvironment() {
        isNight();
        isOutside();
        getForestDensity();
        isInCave();
        getCaveDepth();
        isInDangerousStructure();
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getSpawnPos() {
        return spawnPos;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public MobSpawnType getSpawnType() {
        return spawnType;
    }

    public boolean isNight() {
        if (isNight == null) {
            isNight = !level.isDay();
        }
        return isNight;
    }

    public boolean isOutside() {
        if (isOutside == null) {
            isOutside = LocationDetector.isOutside(level, spawnPos);
        }
        return isOutside;
    }

    public double getForestDensity() {
        if (forestDensity == null) {
            forestDensity = LocationDetector.getForestDensity(level, spawnPos);
        }
        return forestDensity;
    }

    public boolean isInCave() {
        if (isInCave == null) {
            isInCave = LocationDetector.isInCave(level, spawnPos);
        }
        return isInCave;
    }

    public double getCaveDepth() {
        if (caveDepth == null) {
            caveDepth = LocationDetector.getCaveDepth(spawnPos);
        }
        return caveDepth;
    }

    public boolean isInDangerousStructure() {
        if (isInDangerousStructure == null) {
            isInDangerousStructure = LocationDetector.isInDangerousStructure(level, spawnPos);
        }
        return isInDangerousStructure;
    }
}
