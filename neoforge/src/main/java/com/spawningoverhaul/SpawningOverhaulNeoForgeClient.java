package com.spawningoverhaul;

import com.spawningoverhaul.config.YACLConfigScreenFactory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Client-only mod entrypoint for NeoForge.
 * This class is only loaded on the physical client, never on dedicated servers.
 */
@Mod(value = SpawningOverhaulCommon.MOD_ID, dist = Dist.CLIENT)
public class SpawningOverhaulNeoForgeClient {

    public SpawningOverhaulNeoForgeClient(ModContainer modContainer) {
        // Register config screen (safe here - this class only loads on client)
        modContainer.registerExtensionPoint(
            IConfigScreenFactory.class,
            (container, parent) -> YACLConfigScreenFactory.createConfigScreen(parent)
        );
    }
}
