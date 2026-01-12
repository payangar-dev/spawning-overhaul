package com.spawningoverhaul.config;

import com.spawningoverhaul.SpawningOverhaulCommon;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration class for immersive spawning mechanics using YACL.
 */
public class SpawningConfig {
    // Platform-specific path will be provided by the loader modules
    private static Path configPath;
    private static ConfigClassHandler<SpawningConfig> handler;

    public static void setConfigPath(Path path) {
        configPath = path;
    }

    public static ConfigClassHandler<SpawningConfig> HANDLER() {
        if (handler == null) {
            handler = ConfigClassHandler.createBuilder(SpawningConfig.class)
                .id(ResourceLocation.fromNamespaceAndPath(SpawningOverhaulCommon.MOD_ID, "config"))
                .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(getConfigPath())
                    .setJson5(true)
                    .build())
                .build();
        }
        return handler;
    }

    private static Path getConfigPath() {
        if (configPath == null) {
            throw new IllegalStateException("Config path not set! Call setConfigPath first.");
        }
        return configPath.resolve("spawningoverhaul.json5");
    }

    // ===== Immersive Spawning Settings =====

    @SerialEntry(comment = "Enable immersive spawning system")
    public boolean enableImmersiveSpawning = true;

    @SerialEntry(comment = "Base spawn rate multiplier (default spawn rate everywhere)")
    public double baseMultiplier = 0.2;

    @SerialEntry(comment = "Minimum spawn rate multiplier in caves (at shallowest detected depth)")
    public double minCaveMultiplier = 1.0;

    @SerialEntry(comment = "Maximum spawn rate multiplier in deep caves (at deepest depths)")
    public double maxCaveMultiplier = 5.0;

    @SerialEntry(comment = "Minimum spawn rate multiplier in forests (at lowest density)")
    public double minForestMultiplier = 1.0;

    @SerialEntry(comment = "Maximum spawn rate multiplier in dense forests (at highest density)")
    public double maxForestMultiplier = 3.0;

    @SerialEntry(comment = "Base multiplier for dangerous structures")
    public double dangerousStructureMultiplier = 3.0;

    @SerialEntry(comment = "Number of logs required in radius to count as dense forest")
    public int denseForestLogThreshold = 50;

    @SerialEntry(comment = "Radius (in blocks) to count logs for dense forest detection")
    public int denseForestScanRadius = 10;

    @SerialEntry(comment = "Enable structure-based spawn modifications")
    public boolean enableStructureModifications = true;

    // ===== Mob-Specific Rules =====

    @SerialEntry(comment = "If enabled, spiders can only spawn in caves")
    public boolean spiderOnlyInCave = true;

    @SerialEntry(comment = "If enabled, creepers cannot spawn near structures (prevents griefing)")
    public boolean disableCreeperNearStructure = false;

    // ===== Mod Compatibility =====

    @SerialEntry(comment = "Additional structure IDs to treat as dangerous (e.g., 'modid:structure_name')")
    public List<String> additionalDangerousStructures = new ArrayList<>();

    @SerialEntry(comment = "Additional structure IDs to treat as safe (e.g., 'modid:structure_name')")
    public List<String> additionalSafeStructures = new ArrayList<>();

    @SerialEntry(comment = "Mob-specific spawn multipliers (e.g., 'modid:mob_name' -> 2.0)")
    public Map<String, Double> mobSpecificMultipliers = new HashMap<>();

    // Methods

    public static void load() {
        HANDLER().load();
        SpawningOverhaulCommon.getLogger().info("Loaded configuration from {}", getConfigPath());
    }

    public static void save() {
        HANDLER().save();
        SpawningOverhaulCommon.getLogger().info("Saved configuration to {}", getConfigPath());
    }
}
