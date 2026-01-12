package com.spawningoverhaul.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Biome-specific spawn configuration.
 * Allows fine-grained control over spawning in different biomes.
 */
public class BiomeSpawnConfig {
    public Map<String, Double> biomeMultipliers = createDefaultBiomeMultipliers();
    public List<String> hostileSpawnBlacklistedBiomes = List.of("minecraft:mushroom_fields");
    public List<String> passiveSpawnBoostedBiomes = List.of("minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow");

    private static Map<String, Double> createDefaultBiomeMultipliers() {
        Map<String, Double> defaults = new HashMap<>();
        defaults.put("minecraft:plains", 1.2);
        defaults.put("minecraft:desert", 0.7);
        defaults.put("minecraft:forest", 1.1);
        defaults.put("minecraft:taiga", 1.0);
        defaults.put("minecraft:swamp", 1.3);
        defaults.put("minecraft:jungle", 1.4);
        defaults.put("minecraft:nether_wastes", 1.5);
        return defaults;
    }
}
