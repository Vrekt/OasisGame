package me.vrekt.oasis.network.server.cache;

import com.badlogic.gdx.math.Vector2;
import me.vrekt.oasis.entity.GameEntity;

/**
 * Basic capture of an entity.
 */
public final class EntityStateCache {

    private final Vector2 position = new Vector2();

    public EntityStateCache(GameEntity entity) {
        position.set(entity.getPosition());
    }

    /**
     * @return position
     */
    public Vector2 position() {
        return position;
    }
}
