package me.vrekt.oasis.world;

import gdx.lunar.server.world.config.ServerWorldConfiguration;
import gdx.lunar.server.world.impl.WorldAdapter;

/**
 * Represents a game world within Crimson
 */
public final class CrimsonWorld extends WorldAdapter {

    public CrimsonWorld(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }

    public CrimsonWorld() {
    }
}
