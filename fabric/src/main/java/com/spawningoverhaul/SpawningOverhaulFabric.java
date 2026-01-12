package com.spawningoverhaul;

import com.spawningoverhaul.command.SpawnDebugCommand;
import com.spawningoverhaul.config.SpawningConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawningOverhaulFabric implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpawningOverhaulCommon.MOD_NAME);

    @Override
    public void onInitialize() {
        // Set up logger for common code
        SpawningOverhaulCommon.setLogger(new SpawningOverhaulCommon.Logger() {
            @Override
            public void info(String message, Object... args) {
                LOGGER.info(String.format(message.replace("{}", "%s"), args));
            }
        });

        LOGGER.info("Initializing {} for Fabric", SpawningOverhaulCommon.MOD_NAME);

        // Set config path for Fabric
        SpawningConfig.setConfigPath(FabricLoader.getInstance().getConfigDir());

        // Initialize common code
        SpawningOverhaulCommon.init();

        // Fabric-specific initialization
        registerFabricEvents();
        registerCommands();
    }

    private void registerFabricEvents() {
        // Spawn handling uses mixins (see SpawnHelperMixin)
        LOGGER.info("Spawn handling configured via mixins");
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SpawnDebugCommand.register(dispatcher);
        });
        LOGGER.info("Registered /spawndebug command");
    }
}
