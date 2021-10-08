package me.vrekt.oasis.world.management;

import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.world.AbstractWorld;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages contexts of worlds.
 */
public final class WorldManager implements Disposable {

    private final Map<String, AbstractWorld> worlds = new HashMap<>();

    private AbstractWorld world;

    public void registerWorld(String name, AbstractWorld world) {
        this.worlds.put(name, world);
    }

    public <T extends AbstractWorld> T getWorld(String name) {
        return (T) worlds.get(name);
    }

    public void setWorld(AbstractWorld world) {
        if (this.world != null) world.dispose();
        this.world = world;
    }

    @Override
    public void dispose() {
        if (world != null) world.dispose();
    }
}
