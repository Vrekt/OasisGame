package me.vrekt.oasis.network.server.cache;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.entity.GameEntity;
import me.vrekt.oasis.world.GameWorld;

import java.util.Iterator;

/**
 * Capture of the local state
 */
public final class GameStateSnapshot implements Pool.Poolable {

    private static final Pool<GameStateSnapshot> POOL = new Pool<>(10) {
        @Override
        protected GameStateSnapshot newObject() {
            return new GameStateSnapshot();
        }
    };

    /**
     * Create a new cache snapshot
     *
     * @param world the world
     * @return the snapshot
     */
    public static GameStateSnapshot of(GameWorld world) {
        final GameStateSnapshot cache = POOL.obtain();
        return cache.capture(world);
    }

    private final IntMap<EntityStateCache> entities = new IntMap<>();

    private GameStateSnapshot() {
    }

    /**
     * Capture the world snapshot.
     *
     * @param world the world
     * @return this
     */
    private GameStateSnapshot capture(GameWorld world) {
        entities.clear();
        // check for any new changes.
        // TODO: Destroy entity/object should be updated here.
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

    /**
     * Free
     */
    public void free() {
        POOL.free(this);
    }

    @Override
    public void reset() {
        entities.clear();
    }
}
