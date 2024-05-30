package me.vrekt.oasis.world.effects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.Entity;

/**
 * Represents an area effect cloud.
 * Any entity within this cloud is effected by it.
 */
public final class AreaEffectCloud implements Pool.Poolable {

    private static final Pool<AreaEffectCloud> POOL = new Pool<>() {
        @Override
        protected AreaEffectCloud newObject() {
            return new AreaEffectCloud();
        }
    };

    private Effect effect;
    private float tickApplied;
    private float duration;
    private final Rectangle bounds = new Rectangle();
    private final IntMap<EntityAreaTracker> affected = new IntMap<>();

    private Entity immune;

    /**
     * Create a new area effect cloud
     *
     * @param type     type
     * @param position position
     * @param strength strength
     * @param duration seconds
     * @return the new area effect
     */
    public static AreaEffectCloud create(EffectType type,
                                         Vector2 position,
                                         float interval,
                                         float strength,
                                         float duration) {
        final AreaEffectCloud effectCloud = POOL.obtain();
        final Effect effect = Effect.create(type, interval, strength, 0.0f);

        effectCloud.load(effect, position, duration, null);
        return effectCloud;
    }

    /**
     * Create a new area effect cloud
     *
     * @param type     type
     * @param position position
     * @param strength strength
     * @param duration seconds
     * @param immune   the entity immune
     * @return the new area effect
     */
    public static AreaEffectCloud create(EffectType type,
                                         Vector2 position,
                                         float interval,
                                         float strength,
                                         float duration,
                                         Entity immune) {
        final AreaEffectCloud effectCloud = POOL.obtain();
        final Effect effect = Effect.create(type, interval, strength, 0.0f);

        effectCloud.load(effect, position, duration, immune);
        return effectCloud;
    }

    void load(Effect effect, Vector2 position, float duration, Entity immune) {
        this.effect = effect;
        this.duration = duration;
        this.bounds.set(position.x, position.y, 6, 6);
        this.tickApplied = GameManager.getTick();
        this.immune = immune;
    }

    /**
     * Process an entity updating their status within this cloud
     *
     * @param entity the entity
     */
    public void process(Entity entity) {
        if (immune != null && immune.getEntityId() == entity.getEntityId()) return;

        if (affected.containsKey(entity.getEntityId())) {
            if (!isEntityInside(entity)) {
                affected.remove(entity.getEntityId());
            }
        } else {
            if (isEntityInside(entity)) {
                affected.put(entity.getEntityId(), new EntityAreaTracker(entity));
            }
        }
    }

    /**
     * @param entity entity test
     * @return {@code true} if the entity is inside this effect cloud
     */
    public boolean isEntityInside(Entity entity) {
        return entity.bb().overlaps(bounds);
    }

    /**
     * Update this effect cloud
     *
     * @return {@code true} if this cloud is expired/done.
     */
    public boolean update() {
        float now = GameManager.getTick();
        for (EntityAreaTracker entity : affected.values()) {
            if (effect.ready(entity.lastApplied)) {
                entity.lastApplied = now;
                effect.apply(entity.entity);
            }
        }

        return GameManager.hasTimeElapsed(tickApplied, duration);
    }

    /**
     * dispose
     */
    public void dispose() {
        effect.free();
        POOL.free(this);
    }

    @Override
    public void reset() {
        effect = null;
        tickApplied = 0.0f;

        immune = null;
        bounds.set(0, 0, 0, 0);
        affected.clear();
    }

    private static final class EntityAreaTracker {
        final Entity entity;
        float lastApplied;

        EntityAreaTracker(Entity entity) {
            this.entity = entity;
        }
    }

}
