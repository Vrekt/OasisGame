package me.vrekt.oasis.world.systems;

import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.Entity;

/**
 * Represents a world system that updates something
 */
public abstract class WorldSystem {

    protected int systemId;
    protected float updateInterval;
    protected float lastUpdateInterval;

    public WorldSystem(int systemId, float updateInterval) {
        this.systemId = systemId;
        this.updateInterval = updateInterval;
    }

    /**
     * @return {@code true} if this system is ready to update
     */
    public boolean readyToUpdate() {
        return GameManager.hasTimeElapsed(lastUpdateInterval, updateInterval);
    }

    /**
     * Update
     *
     * @param delta GDX delta
     * @param tick  world tick
     */
    public void update(float delta, float tick) {
        lastUpdateInterval = tick;
        process(delta, tick);
    }

    /**
     * receive an entity
     *
     * @param entity the entity
     */
    protected void receive(Entity entity) {

    }

    protected abstract void process(float delta, float tick);

}
