package com.spawningoverhaul.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YACLConfigScreenFactory {

    public static Screen createConfigScreen(Screen parent) {
        return YetAnotherConfigLib.create(SpawningConfig.HANDLER(), (defaults, config, builder) -> {
            // Environmental Multipliers Category
            var environmentCategory = ConfigCategory.createBuilder()
                .name(Component.literal("Environmental Multipliers"))
                .tooltip(Component.literal("Configure spawn rates based on environment"))
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("General"))
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Enable Immersive Spawning"))
                        .description(OptionDescription.of(Component.literal("Enable the immersive spawning system")))
                        .binding(defaults.enableImmersiveSpawning,
                                () -> config.enableImmersiveSpawning,
                                value -> config.enableImmersiveSpawning = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Double>createBuilder()
                        .name(Component.literal("Base Multiplier"))
                        .description(OptionDescription.of(Component.literal("Base spawn rate multiplier applied everywhere (before cave/forest adjustments)")))
                        .binding(defaults.baseMultiplier,
                                () -> config.baseMultiplier,
                                value -> config.baseMultiplier = value)
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                            .range(0.0, 2.0)
                            .step(0.1))
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Environment Rates"))
                    .option(Option.<Double>createBuilder()
                        .name(Component.literal("Min Cave Multiplier"))
                        .description(OptionDescription.of(Component.literal("Minimum spawn rate in caves (at shallowest depth)")))
                        .binding(defaults.minCaveMultiplier,
                                () -> config.minCaveMultiplier,
                                value -> config.minCaveMultiplier = value)
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                            .range(0.0, 5.0)
                            .step(0.1))
                        .build())
                    .option(Option.<Double>createBuilder()
                        .name(Component.literal("Max Cave Multiplier"))
                        .description(OptionDescription.of(Component.literal("Maximum spawn rate in deep caves (at deepest depths)")))
                        .binding(defaults.maxCaveMultiplier,
                                () -> config.maxCaveMultiplier,
                                value -> config.maxCaveMultiplier = value)
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                            .range(0.5, 10.0)
                            .step(0.5))
                        .build())
                    .option(Option.<Double>createBuilder()
                        .name(Component.literal("Min Forest Multiplier"))
                        .description(OptionDescription.of(Component.literal("Minimum spawn rate in forests (at lowest density)")))
                        .binding(defaults.minForestMultiplier,
                                () -> config.minForestMultiplier,
                                value -> config.minForestMultiplier = value)
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                            .range(0.0, 5.0)
                            .step(0.1))
                        .build())
                    .option(Option.<Double>createBuilder()
                        .name(Component.literal("Max Forest Multiplier"))
                        .description(OptionDescription.of(Component.literal("Maximum spawn rate in dense forests (at highest density)")))
                        .binding(defaults.maxForestMultiplier,
                                () -> config.maxForestMultiplier,
                                value -> config.maxForestMultiplier = value)
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                            .range(0.5, 10.0)
                            .step(0.5))
                        .build())
                    .option(Option.<Double>createBuilder()
                        .name(Component.literal("Dangerous Structure Multiplier"))
                        .description(OptionDescription.of(Component.literal("Spawn rate multiplier in dangerous structures (strongholds, fortresses, etc.)")))
                        .binding(defaults.dangerousStructureMultiplier,
                                () -> config.dangerousStructureMultiplier,
                                value -> config.dangerousStructureMultiplier = value)
                        .controller(opt -> DoubleSliderControllerBuilder.create(opt)
                            .range(0.5, 10.0)
                            .step(0.5))
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Enable Structure Modifications"))
                        .description(OptionDescription.of(Component.literal("Enable spawn modifications based on structures")))
                        .binding(defaults.enableStructureModifications,
                                () -> config.enableStructureModifications,
                                value -> config.enableStructureModifications = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build();

            // Mob-Specific Rules Category
            var mobRulesCategory = ConfigCategory.createBuilder()
                .name(Component.literal("Mob-Specific Rules"))
                .tooltip(Component.literal("Configure spawn rules for specific mob types"))
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Mob Restrictions"))
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Spider Only In Cave"))
                        .description(OptionDescription.of(Component.literal("If enabled, spiders can only spawn in caves")))
                        .binding(defaults.spiderOnlyInCave,
                                () -> config.spiderOnlyInCave,
                                value -> config.spiderOnlyInCave = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.<Boolean>createBuilder()
                        .name(Component.literal("Disable Creeper Near Structure"))
                        .description(OptionDescription.of(Component.literal("If enabled, creepers cannot spawn near structures (prevents griefing)")))
                        .binding(defaults.disableCreeperNearStructure,
                                () -> config.disableCreeperNearStructure,
                                value -> config.disableCreeperNearStructure = value)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build();

            // Detection Settings Category
            var detectionCategory = ConfigCategory.createBuilder()
                .name(Component.literal("Detection Settings"))
                .tooltip(Component.literal("Configure environment detection parameters"))
                .group(OptionGroup.createBuilder()
                    .name(Component.literal("Forest Detection"))
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Dense Forest Log Threshold"))
                        .description(OptionDescription.of(Component.literal("Number of logs required in radius to count as dense forest")))
                        .binding(defaults.denseForestLogThreshold,
                                () -> config.denseForestLogThreshold,
                                value -> config.denseForestLogThreshold = value)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                            .range(10, 200)
                            .step(5))
                        .build())
                    .option(Option.<Integer>createBuilder()
                        .name(Component.literal("Dense Forest Scan Radius"))
                        .description(OptionDescription.of(Component.literal("Radius (in blocks) to scan for logs when detecting dense forests")))
                        .binding(defaults.denseForestScanRadius,
                                () -> config.denseForestScanRadius,
                                value -> config.denseForestScanRadius = value)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                            .range(5, 30)
                            .step(1))
                        .build())
                    .build())
                .build();

            return builder
                .title(Component.literal("Spawning Overhaul Configuration"))
                .category(environmentCategory)
                .category(mobRulesCategory)
                .category(detectionCategory);
        }).generateScreen(parent);
    }
}
