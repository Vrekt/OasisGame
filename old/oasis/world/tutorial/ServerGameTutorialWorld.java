package me.vrekt.oasis.world.tutorial;

import gdx.lunar.server.world.config.ServerWorldConfiguration;
import me.vrekt.oasis.world.CrimsonWorld;

/**
 * Handles multiplayer within the tutorial world of the game
 */
public final class ServerGameTutorialWorld extends CrimsonWorld {

    public ServerGameTutorialWorld(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }

}
