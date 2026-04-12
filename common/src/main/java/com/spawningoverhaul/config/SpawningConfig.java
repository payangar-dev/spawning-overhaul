package com.spawningoverhaul.config;

import com.spawningoverhaul.SpawningOverhaulCommon;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

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

    // ===== General Settings =====

    @AutoGen(category = "general")
    @SerialEntry(comment = "Enable debugging")
    @TickBox
    public boolean debugmode = false;

    @AutoGen(category = "general")
    @SerialEntry(comment = "Enable immersive spawning system")
    @TickBox
    public boolean enableImmersiveSpawning = true;

    // ===== Dimension Whitelist =====

    @AutoGen(category = "dimensions")
    @ListGroup(valueFactory = DimensionListFactory.class, controllerFactory = DimensionListFactory.class)
    @SerialEntry(comment = "Dimension IDs where immersive spawning is active (vanilla or modded, e.g., 'minecraft:overworld', 'twilightforest:twilight_forest')")
    public List<String> enabledDimensions = new ArrayList<>(List.of("minecraft:overworld"));

    // ===== Environmental Multipliers =====

    @AutoGen(category = "environment")
    @SerialEntry(comment = "Base spawn rate multiplier (default spawn rate everywhere)")
    @DoubleSlider(min = 0.0, max = 2.0, step = 0.1)
    public double baseMultiplier = 0.2;

    @AutoGen(category = "environment")
    @SerialEntry(comment = "Minimum spawn rate multiplier in caves (at shallowest detected depth)")
    @DoubleSlider(min = 0.0, max = 5.0, step = 0.1)
    public double minCaveMultiplier = 1.0;

    @AutoGen(category = "environment")
    @SerialEntry(comment = "Maximum spawn rate multiplier in deep caves (at deepest depths)")
    @DoubleSlider(min = 0.5, max = 10.0, step = 0.5)
    public double maxCaveMultiplier = 5.0;

    @AutoGen(category = "environment")
    @SerialEntry(comment = "Minimum spawn rate multiplier in forests (at lowest density)")
    @DoubleSlider(min = 0.0, max = 5.0, step = 0.1)
    public double minForestMultiplier = 1.0;

    @AutoGen(category = "environment")
    @SerialEntry(comment = "Maximum spawn rate multiplier in dense forests (at highest density)")
    @DoubleSlider(min = 0.5, max = 10.0, step = 0.5)
    public double maxForestMultiplier = 3.0;

    @AutoGen(category = "environment")
    @SerialEntry(comment = "Base multiplier for dangerous structures")
    @DoubleSlider(min = 0.5, max = 10.0, step = 0.5)
    public double dangerousStructureMultiplier = 3.0;

    @AutoGen(category = "environment")
    @SerialEntry(comment = "Enable structure-based spawn modifications")
    @TickBox
    public boolean enableStructureModifications = true;

    // ===== Detection Settings =====

    @AutoGen(category = "detection")
    @SerialEntry(comment = "Number of logs required in radius to count as dense forest")
    @IntSlider(min = 10, max = 200, step = 5)
    public int denseForestLogThreshold = 50;

    @AutoGen(category = "detection")
    @SerialEntry(comment = "Radius (in blocks) to count logs for dense forest detection")
    @IntSlider(min = 5, max = 30, step = 1)
    public int denseForestScanRadius = 10;

    // ===== Mob-Specific Rules =====

    @AutoGen(category = "mobs")
    @SerialEntry(comment = "If enabled, spiders can only spawn in caves")
    @TickBox
    public boolean spiderOnlyInCave = true;

    @AutoGen(category = "mobs")
    @SerialEntry(comment = "If enabled, creepers cannot spawn near structures (prevents griefing)")
    @TickBox
    public boolean disableCreeperNearStructure = false;

    // ===== Mod Compatibility (no GUI for complex types) =====

    @SerialEntry(comment = "Additional structure IDs to treat as dangerous (e.g., 'modid:structure_name')")
    public List<String> additionalDangerousStructures = new ArrayList<>();

    @SerialEntry(comment = "Additional structure IDs to treat as safe (e.g., 'modid:structure_name')")
    public List<String> additionalSafeStructures = new ArrayList<>();

    @SerialEntry(comment = "Mob-specific spawn multipliers (e.g., 'modid:mob_name' -> 2.0)")
    public Map<String, Double> mobSpecificMultipliers = new HashMap<>();

    // Methods

    /**
     * Check whether the immersive spawning system should run in the given dimension.
     * A dimension is active only if its ID is present in {@link #enabledDimensions}.
     */
    public boolean isDimensionEnabled(ResourceKey<Level> dimension) {
        return enabledDimensions.contains(dimension.location().toString());
    }

    public static void load() {
        HANDLER().load();
        SpawningOverhaulCommon.getLogger().info("Loaded configuration from {}", getConfigPath());
    }

    public static void save() {
        HANDLER().save();
        SpawningOverhaulCommon.getLogger().info("Saved configuration to {}", getConfigPath());
    }

    public static class DimensionListFactory implements ListGroup.ValueFactory<String>, ListGroup.ControllerFactory<String> {
        @Override
        public String provideNewValue() {
            return "minecraft:";
        }

        @Override
        public ControllerBuilder<String> createController(ListGroup annotation, ConfigField<List<String>> field, OptionAccess storage, Option<String> option) {
            return StringControllerBuilder.create(option);
        }
    }
}
