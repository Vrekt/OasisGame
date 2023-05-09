package me.vrekt.oasis.world;

import com.badlogic.gdx.math.Vector2;
import gdx.lunar.server.world.config.ServerWorldConfiguration;
import gdx.lunar.server.world.impl.WorldAdapter;
import me.vrekt.shared.entities.EntityType;

/**
 * Represents a game world within Crimson
 */
public final class CrimsonWorld extends WorldAdapter {

    public CrimsonWorld(ServerWorldConfiguration configuration, String worldName) {
        super(configuration, worldName);
    }

    public void spawnEntityInWorld(EntityType type, Vector2 position) {
        final int entityId = assignEntityId();

    }
}
