package me.vrekt.crimson.game.world;


import com.badlogic.gdx.utils.Disposable;
import me.vrekt.crimson.game.world.interior.InteriorWorld;
import me.vrekt.oasis.world.interior.InteriorWorldType;

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
    private final Map<InteriorWorldType, InteriorWorld> interiorWorlds = new HashMap<>();

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
     * Add a world
     *
     * @param type  the type
     * @param world the world
     */
    public void addInteriorWorld(InteriorWorldType type, InteriorWorld world) {
        this.interiorWorlds.put(type, world);
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

    public InteriorWorld getInteriorWorld(InteriorWorldType type) {
        return interiorWorlds.get(type);
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

    public Collection<InteriorWorld> getInteriorWorlds() {
        return interiorWorlds.values();
    }

    /**
     * Update all worlds.
     */
    public void update() {
        for (World world : worlds.values()) {
            world.tick();
        }

        for (InteriorWorld world : interiorWorlds.values()) {
            world.tick();
        }
    }

    @Override
    public void dispose() {
        getWorlds().forEach(World::dispose);
        getInteriorWorlds().forEach(World::dispose);
        worlds.clear();
        interiorWorlds.clear();
    }

}
