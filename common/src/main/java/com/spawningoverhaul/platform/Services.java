package com.spawningoverhaul.platform;

import java.nio.file.Path;

/**
 * Platform-agnostic service interface for loader-specific functionality.
 */
public interface Services {

    /**
     * Get the config directory for the current platform.
     */
    Path getConfigDir();

    /**
     * Check if the mod is loaded in a development environment.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Get the loader name (e.g., "Fabric", "NeoForge").
     */
    String getLoaderName();
}
