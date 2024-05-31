package me.vrekt.oasis.world;

import gdx.lunar.server.world.AbstractServerWorld;
import gdx.lunar.server.world.config.ServerWorldConfiguration;

/**
 * Represents a server world that may be extended.
 */
public abstract class CrimsonServerWorld extends AbstractServerWorld {

    public CrimsonServerWorld(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }


}
