package com.spawningoverhaul.config;

import net.minecraft.client.gui.screens.Screen;

public class YACLConfigScreenFactory {

    public static Screen createConfigScreen(Screen parent) {
        return ConfigScreenBuilder.createConfigScreen(parent);
    }
}
