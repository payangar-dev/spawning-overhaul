package com.spawningoverhaul;

import com.spawningoverhaul.command.SpawnDebugCommand;
import com.spawningoverhaul.config.SpawningConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SpawningOverhaulCommon.MOD_ID)
public class SpawningOverhaulNeoForge {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpawningOverhaulCommon.MOD_NAME);

    public SpawningOverhaulNeoForge(IEventBus modBus, ModContainer modContainer) {
        // Set up logger for common code
        SpawningOverhaulCommon.setLogger(new SpawningOverhaulCommon.Logger() {
            @Override
            public void info(String message, Object... args) {
                LOGGER.info(String.format(message.replace("{}", "%s"), args));
            }
        });

        LOGGER.info("Initializing {} for NeoForge", SpawningOverhaulCommon.MOD_NAME);

        // Set config path for NeoForge
        SpawningConfig.setConfigPath(FMLPaths.CONFIGDIR.get());

        // Initialize common code
        SpawningOverhaulCommon.init();

        // NeoForge-specific initialization
        registerNeoForgeEvents(modBus);

        // Register config screen
        modContainer.registerExtensionPoint(
            net.neoforged.neoforge.client.gui.IConfigScreenFactory.class,
            (container, parent) -> com.spawningoverhaul.config.YACLConfigScreenFactory.createConfigScreen(parent)
        );
    }

    private void registerNeoForgeEvents(IEventBus modBus) {
        // Register spawn event handler to the game event bus (not mod bus)
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(new com.spawningoverhaul.event.SpawnEventHandler());
        LOGGER.info("Registered NeoForge spawn event handler for immersive spawning");

        // Register command event handler
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(new CommandRegistrationHandler());
        LOGGER.info("Registered command event handler");
    }

    // Inner class to handle command registration
    public static class CommandRegistrationHandler {
        @SubscribeEvent
        public void onRegisterCommands(RegisterCommandsEvent event) {
            SpawnDebugCommand.register(event.getDispatcher());
        }
    }
}

