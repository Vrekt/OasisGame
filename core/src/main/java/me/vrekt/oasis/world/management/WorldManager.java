package me.vrekt.oasis.world.management;

import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.world.LunarWorld;
import me.vrekt.oasis.world.GameWorld;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles loading and unloading worlds.
 */
public final class WorldManager implements Disposable {

    private final Map<String, GameWorld> worldMap = new ConcurrentHashMap<>();

    public void addWorld(String worldName, GameWorld world) {
        worldMap.put(worldName, world);
    }

    public <T extends GameWorld> T getWorld(String name) {
        return (T) worldMap.get(name);
    }

    public boolean doesWorldExist(String name) {
        return worldMap.containsKey(name);
    }

    @Override
    public void dispose() {
        worldMap.values().forEach(LunarWorld::dispose);
        worldMap.clear();
    }
}
