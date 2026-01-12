package com.spawningoverhaul.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.spawningoverhaul.config.SpawningConfig;
import com.spawningoverhaul.spawn.LocationDetector;
import com.spawningoverhaul.spawn.SpawnContext;
import com.spawningoverhaul.spawn.SpawnMultiplierCalculator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

/**
 * Debug command to display spawn environment information at the player's location.
 * Usage: /spawndebug
 */
public class SpawnDebugCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("spawndebug")
                .requires(source -> source.hasPermission(2)) // Requires OP level 2
                .executes(SpawnDebugCommand::execute)
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        // Check if we're on the server side
        if (!(source.getLevel() instanceof ServerLevel serverLevel)) {
            source.sendFailure(Component.literal("This command can only be used on the server side"));
            return 0;
        }

        // Get player position
        BlockPos pos = BlockPos.containing(source.getPosition());

        // Get config
        SpawningConfig config = SpawningConfig.HANDLER().instance();

        // Create a spawn context for a generic hostile mob (zombie) at this position
        SpawnContext spawnContext = new SpawnContext(serverLevel, pos, EntityType.ZOMBIE, MobSpawnType.NATURAL);

        // Detect all environment properties
        spawnContext.detectEnvironment();

        // Calculate spawn multiplier
        double multiplier = SpawnMultiplierCalculator.calculateMultiplier(spawnContext);

        // Build debug output
        source.sendSuccess(() -> Component.literal("§6=== Spawn Debug Info ==="), false);
        source.sendSuccess(() -> Component.literal("§ePosition: §f" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()), false);
        source.sendSuccess(() -> Component.literal("§eImmersive Spawning: §f" + (config.enableImmersiveSpawning ? "§aEnabled" : "§cDisabled")), false);
        source.sendSuccess(() -> Component.literal(""), false);

        source.sendSuccess(() -> Component.literal("§6Environment:"), false);
        source.sendSuccess(() -> Component.literal("  §eIs Night: §f" + formatBoolean(spawnContext.isNight())), false);
        source.sendSuccess(() -> Component.literal("  §eIs Cave: §f" + formatBoolean(spawnContext.isInCave())), false);
        source.sendSuccess(() -> Component.literal("  §eCave Depth: §f" + String.format("%.2f", spawnContext.getCaveDepth())), false);
        source.sendSuccess(() -> Component.literal("  §eForest Density: §f" + String.format("%.1f%%", spawnContext.getForestDensity() * 100.0)), false);
        source.sendSuccess(() -> Component.literal("  §eDangerous Structure: §f" + formatBoolean(spawnContext.isInDangerousStructure())), false);
        source.sendSuccess(() -> Component.literal(""), false);

        source.sendSuccess(() -> Component.literal("§6Spawn Calculation:"), false);

        // Base multiplier
        double baseMultiplier = config.baseMultiplier;
        source.sendSuccess(() -> Component.literal("  §7Base: §f" + String.format("%.2fx", baseMultiplier)), false);

        // Calculate cave contribution
        double caveContribution = 0.0;
        if (spawnContext.isInCave()) {
            double depth = spawnContext.getCaveDepth();
            double depthRatio = (depth - 1.0) / 4.0;
            caveContribution = config.minCaveMultiplier + (config.maxCaveMultiplier - config.minCaveMultiplier) * depthRatio;
            double finalCaveContribution = caveContribution;
            double finalDepth = depth;
            source.sendSuccess(() -> Component.literal("  §7Cave: §f" + String.format("%.2fx", finalCaveContribution) +
                " §8(depth " + String.format("%.1f", finalDepth) + ", range: " +
                String.format("%.1f-%.1f", config.minCaveMultiplier, config.maxCaveMultiplier) + "x)"), false);
        }

        // Calculate forest contribution
        double forestContribution = 0.0;
        double forestDensity = spawnContext.getForestDensity();
        if (forestDensity > 0.0) {
            forestContribution = config.minForestMultiplier + (config.maxForestMultiplier - config.minForestMultiplier) * forestDensity;
            double finalForestContribution = forestContribution;
            double finalForestDensity = forestDensity;
            source.sendSuccess(() -> Component.literal("  §7Forest: §f" + String.format("%.2fx", finalForestContribution) +
                " §8(density " + String.format("%.0f%%", finalForestDensity * 100.0) + ", range: " +
                String.format("%.1f-%.1f", config.minForestMultiplier, config.maxForestMultiplier) + "x)"), false);
        }

        // Show which multiplier is used
        double environmentMultiplier = Math.max(baseMultiplier, Math.max(caveContribution, forestContribution));
        String source_text;
        if (environmentMultiplier == caveContribution && caveContribution > 0) {
            source_text = "cave";
        } else if (environmentMultiplier == forestContribution && forestContribution > 0) {
            source_text = "forest";
        } else {
            source_text = "base";
        }
        String finalSourceText = source_text;
        source.sendSuccess(() -> Component.literal("  §eUsing: §a" + finalSourceText + " §f→ §a" + String.format("%.2fx", environmentMultiplier)), false);

        // Dangerous structure multiplier (applied on top)
        if (config.enableStructureModifications && spawnContext.isInDangerousStructure()) {
            source.sendSuccess(() -> Component.literal("  §7Structure: §f×" + config.dangerousStructureMultiplier), false);
        }

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§6Final Spawn Multiplier: §f" + String.format("%.2fx", multiplier)), false);

        // Calculate spawn chance
        if (multiplier < 1.0) {
            double spawnChance = multiplier * 100.0;
            source.sendSuccess(() -> Component.literal("§6Spawn Chance: §f" + String.format("%.1f%%", spawnChance)), false);
        } else if (multiplier > 1.0) {
            source.sendSuccess(() -> Component.literal("§6Spawn Rate: §f" + String.format("%.1f%%", multiplier * 100.0) + " of normal"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§6Spawn Rate: §fNormal (100%)"), false);
        }

        return 1;
    }

    private static String formatBoolean(boolean value) {
        return value ? "§aYes" : "§cNo";
    }
}
