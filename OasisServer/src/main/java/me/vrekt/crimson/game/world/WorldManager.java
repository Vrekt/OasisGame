package me.vrekt.crimson.game.world;


import com.badlogic.gdx.utils.Disposable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages instances of active worlds within the server
 */
public final class WorldManager implements Disposable {

    /**
     * Map of all worlds
     */
    private final Map<String, World> worlds = new HashMap<>();

    public WorldManager() {

    }

    /**
     * Add a world
     *
     * @param name  the name
     * @param world the world
     */
    public void addWorld(String name, World world) {
        this.worlds.put(name, world);
    }

    /**
     * Retrieve a world by its name
     *
     * @param name the name
     * @return the world
     */
    public World getWorld(String name) {
        return worlds.get(name);
    }

    public boolean worldExists(String name) {
        return worlds.containsKey(name);
    }

    /**
     * @return all worlds
     */
    public Collection<World> getWorlds() {
        return worlds.values();
    }

    /**
     * Update all worlds.
     */
    public void update() {
        for (World world : worlds.values()) {
            world.tick();
        }
    }

    @Override
    public void dispose() {
        getWorlds().forEach(World::dispose);
        worlds.clear();
    }

}
