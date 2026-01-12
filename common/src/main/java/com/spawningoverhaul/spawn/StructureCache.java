package com.spawningoverhaul.spawn;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.spawningoverhaul.config.SpawningConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * High-performance structure detection using Guava cache.
 * Chunk-level granularity (16x16 blocks share cache entry) with 10-second TTL.
 */
public class StructureCache {

    // Hardcoded dangerous structures (increase spawns)
    private static final Set<String> DANGEROUS_STRUCTURES = Set.of(
            "minecraft:stronghold",
            "minecraft:fortress",
            "minecraft:monument",
            "minecraft:mansion",
            "minecraft:mineshaft",
            "minecraft:dungeon"
    );

    // Hardcoded safe structures (no spawn modification)
    private static final Set<String> SAFE_STRUCTURES = Set.of(
            "minecraft:village"
    );

    // Cache: ChunkPos (as Long) -> Boolean (is dangerous structure present)
    // 10 second TTL, chunk-level granularity
    private static final Cache<CacheKey, Boolean> STRUCTURE_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .maximumSize(1000)
            .build();

    /**
     * Check if a position is in a dangerous structure.
     * Uses chunk-level caching for performance.
     *
     * @param level The level/world
     * @param pos The position to check
     * @return true if in a dangerous structure
     */
    public static boolean isInDangerousStructure(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        // Create cache key from chunk coordinates
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        CacheKey key = new CacheKey(serverLevel.dimension(), chunkX, chunkZ);

        // Check cache first
        Boolean cached = STRUCTURE_CACHE.getIfPresent(key);
        if (cached != null) {
            return cached;
        }

        // Cache miss - perform structure lookup
        boolean isDangerous = checkStructureAt(serverLevel, pos);
        STRUCTURE_CACHE.put(key, isDangerous);
        return isDangerous;
    }

    /**
     * Perform actual structure lookup at a position.
     * Checks against hardcoded dangerous/safe structures and config lists.
     *
     * @param level The server level
     * @param pos The position to check
     * @return true if dangerous structure present
     */
    private static boolean checkStructureAt(ServerLevel level, BlockPos pos) {
        SpawningConfig config = SpawningConfig.HANDLER().instance();
        var structureLookup = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);

        // Get all structures at this position
        var structureManager = level.structureManager();

        // Check each registered structure type
        for (var entry : structureLookup.listElements().toList()) {
            ResourceKey<Structure> structureKey = entry.key();
            ResourceLocation structureId = structureKey.location();
            String structureIdString = structureId.toString();

            // Check if structure is present at position
            if (structureManager.getStructureAt(pos, entry.value()).isValid()) {
                // Check if it's a safe structure (skip these)
                if (SAFE_STRUCTURES.contains(structureIdString)) {
                    continue;
                }

                if (config.additionalSafeStructures.contains(structureIdString)) {
                    continue;
                }

                // Check if it's a dangerous structure
                if (DANGEROUS_STRUCTURES.contains(structureIdString)) {
                    return true;
                }

                if (config.additionalDangerousStructures.contains(structureIdString)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Cache key combining dimension and chunk coordinates.
     */
    private record CacheKey(ResourceKey<Level> dimension, int chunkX, int chunkZ) {}
}
