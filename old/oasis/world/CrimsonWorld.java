package me.vrekt.oasis.world;

import gdx.lunar.server.world.config.ServerWorldConfiguration;
import me.vrekt.oasis.entity.CrimsonPlayer;

/**
 * Represents a game world within Crimson
 */
public class CrimsonWorld extends CrimsonServerWorld {

    public CrimsonWorld(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }



    public void handlePlayerLoaded(CrimsonPlayer player) {

    }

}
