package me.vrekt.oasis.world.systems;

import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.GameManager;

/**
 * Represents a world system that updates something
 */
public abstract class WorldSystem implements Disposable {

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
        return updateInterval == 0 || GameManager.hasTimeElapsed(lastUpdateInterval, updateInterval);
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

    protected abstract void process(float delta, float tick);

}
