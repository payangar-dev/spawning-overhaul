package com.spawningoverhaul.config;

import net.minecraft.client.gui.screens.Screen;

/**
 * Shared config screen builder for both Fabric and NeoForge.
 * The screen is auto-generated from @AutoGen annotations in SpawningConfig.
 */
public class ConfigScreenBuilder {

    public static Screen createConfigScreen(Screen parent) {
        return SpawningConfig.HANDLER().generateGui().generateScreen(parent);
    }
}
