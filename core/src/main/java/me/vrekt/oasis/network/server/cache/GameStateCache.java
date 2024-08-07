package me.vrekt.oasis.network.server.cache;

import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.world.GameWorld;

import java.util.Iterator;

/**
 * Capture of the local state
 */
public final class GameStateCache {

    private final IntMap<EntityStateCache> entities = new IntMap<>();

    public GameStateCache capture(GameWorld world) {
        entities.clear();
        // check for any new changes.
        // TODO: Destroy entity/object should be updated here.
        // TODO: Pool Cache
        final Iterator<GameEntity> entries = world.entities().values();
        while (entries.hasNext()) {
            final GameEntity entity = entries.next();

            entities.put(entity.entityId(), new EntityStateCache(entity));
        }

        return this;
    }

    /**
     * @return map of entity captures.
     */
    public IntMap<EntityStateCache> entities() {
        return entities;
    }

}
