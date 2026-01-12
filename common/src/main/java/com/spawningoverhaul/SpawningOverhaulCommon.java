package com.spawningoverhaul;

import com.spawningoverhaul.config.SpawningConfig;
import com.spawningoverhaul.spawn.rules.MobRuleRegistry;

public class SpawningOverhaulCommon {
    public static final String MOD_ID = "spawningoverhaul";
    public static final String MOD_NAME = "Spawning Overhaul";

    // Simple logger interface - loaders will provide implementation
    private static Logger logger = new Logger() {
        @Override
        public void info(String message, Object... args) {
            System.out.printf("[INFO] " + message + "%n", args);
        }
    };

    public static void setLogger(Logger customLogger) {
        logger = customLogger;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void init() {
        logger.info("Initializing {} Common", MOD_NAME);
        SpawningConfig.load();
        initSpawningMechanics();
    }

    private static void initSpawningMechanics() {
        logger.info("Initializing immersive spawning mechanics");

        // Register default mob-specific spawn rules
        MobRuleRegistry.registerDefaults();
        logger.info("Registered default mob spawn rules");

        // Log key configuration values
        SpawningConfig config = SpawningConfig.HANDLER().instance();
        logger.info("Immersive spawning enabled: {}", config.enableImmersiveSpawning);
        logger.info("Base multiplier: {}", config.baseMultiplier);
        logger.info("Cave multiplier range: {} - {}", config.minCaveMultiplier, config.maxCaveMultiplier);
        logger.info("Forest multiplier range: {} - {}", config.minForestMultiplier, config.maxForestMultiplier);
        logger.info("Dangerous structure multiplier: {}", config.dangerousStructureMultiplier);
    }

    public interface Logger {
        void info(String message, Object... args);
    }
}
