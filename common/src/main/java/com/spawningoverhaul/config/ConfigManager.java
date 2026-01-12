package com.spawningoverhaul.config;

/**
 * Manages the spawning configuration using YACL.
 */
public class ConfigManager {
    public static SpawningConfig getInstance() {
        return SpawningConfig.HANDLER().instance();
    }

    public static void load() {
        SpawningConfig.load();
    }

    public static void save() {
        SpawningConfig.save();
    }
}
