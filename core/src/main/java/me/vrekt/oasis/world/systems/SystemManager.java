package me.vrekt.oasis.world.systems;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;

/**
 * Handles systems within a world.
 */
public final class SystemManager implements Disposable {

    private final IntMap<WorldSystem> systems = new IntMap<>();

    /**
     * Add a system to be updated
     *
     * @param system system
     */
    public void add(WorldSystem system) {
        this.systems.put(system.systemId, system);
    }

    /**
     * Get
     *
     * @param systemId ID
     * @return the system
     */
    public WorldSystem get(int systemId) {
        return systems.get(systemId);
    }

    /**
     * Update all systems
     *
     * @param delta delta
     */
    public void update(float delta) {
        final float tick = GameManager.tick();
        for (WorldSystem system : systems.values()) {
            if (system.readyToUpdate()) {
                system.update(delta, tick);
            }
        }
    }

    @Override
    public void dispose() {
        systems.clear();
    }
}
