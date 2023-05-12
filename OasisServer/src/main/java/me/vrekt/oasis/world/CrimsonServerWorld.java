package me.vrekt.oasis.world;

import gdx.lunar.server.world.AbstractServerWorld;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import me.vrekt.oasis.entity.CrimsonEntity;
import me.vrekt.oasis.entity.CrimsonPlayer;

/**
 * Represents a server world that may be extended.
 */
public abstract class CrimsonServerWorld extends AbstractServerWorld<CrimsonPlayer, CrimsonEntity> {

    public CrimsonServerWorld(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }

}
