package me.vrekt.oasis.world.management;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles loading and unloading worlds.
 */
public final class WorldManager implements Disposable {

    private final Map<String, GameWorld> worldMap = new ConcurrentHashMap<>();
    private final Vector2 parentWorldPosition = new Vector2();

    private int memoryManagerTaskId;

    public void addWorld(String worldName, GameWorld world) {
        worldMap.put(worldName, world);
    }

    public GameWorld getWorld(String name) {
        return worldMap.get(name);
    }

    public boolean doesWorldExist(String name) {
        return worldMap.containsKey(name);
    }

    public Map<String, GameWorld> worlds() {
        return worldMap;
    }

    public void setParentWorldPosition(Vector2 position) {
        this.parentWorldPosition.set(position);
    }

    public Vector2 parentWorldPosition() {
        return parentWorldPosition;
    }

    /**
     * Transfer the player to an interior
     *
     * @param player   local player
     * @param parent   the parent world
     * @param interior the interior world
     */
    public void transferIn(PlayerSP player, GameWorld parent, GameWorldInterior interior) {
        parentWorldPosition.set(player.getBody().getPosition());

        GameManager.transitionScreen(parent, interior, () -> {
            interior.loadWorld(false);
            interior.enter();
        });
    }

    /**
     * Transfer the player out of an interior
     *
     * @param player local player
     * @param from   the from interior
     * @param parent the parent world
     */
    public void transferOut(PlayerSP player, GameWorldInterior from, GameWorld parent) {
        player.removeFromInteriorWorld();
        // FIXME:  manageInteriorMemoryState(from);

        player.createBoxBody(parent.boxWorld());
        player.setPosition(parentWorldPosition, true);
        player.updateWorldState(parent);

        GameManager.game().getMultiplexer().addProcessor(parent);

        parent.updateRendererMap();
        GameManager.game().setScreen(parent);

        parent.enter();
    }

    /**
     * Handles unloading the interior
     *
     * @param interior interior
     */
    private void manageInteriorMemoryState(GameWorldInterior interior) {
        if (memoryManagerTaskId != 0) {
            // cancel this task to reset the timer
            GameManager.getTaskManager().cancel(memoryManagerTaskId);
        }
        memoryManagerTaskId = GameManager.getTaskManager().schedule(interior::dispose, GameWorldInterior.UNLOAD_AFTER);
    }

    @Override
    public void dispose() {
        worldMap.values().forEach(GameWorld::dispose);
        worldMap.clear();
    }
}
