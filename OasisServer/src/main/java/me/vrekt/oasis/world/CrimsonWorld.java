package me.vrekt.oasis.world;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import me.vrekt.oasis.entity.CrimsonPlayer;
import me.vrekt.shared.entities.EntityType;

/**
 * Represents a game world within Crimson
 */
public class CrimsonWorld extends CrimsonServerWorld {

    public CrimsonWorld(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }

    public void handlePlayerLoaded(CrimsonPlayer player) {

    }

    public void spawnEntityInWorld(EntityType type, Vector2 position) {

    }
}
