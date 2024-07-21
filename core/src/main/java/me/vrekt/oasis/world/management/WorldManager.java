package me.vrekt.oasis.world.management;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.interior.GameWorldInterior;

/**
 * Handles loading and unloading worlds.
 */
public final class WorldManager implements Disposable {

    private final IntMap<GameWorld> worlds = new IntMap<>();
    // the position where the player entered an interior
    private final Vector2 parentWorldPosition = new Vector2();

    public void addWorld(GameWorld world) {
        worlds.put(world.worldId(), world);
    }

    public GameWorld getWorld(int worldId) {
        return worlds.get(worldId);
    }

    public boolean doesWorldExist(int worldId) {
        return worlds.containsKey(worldId);
    }

    public IntMap<GameWorld> worlds() {
        return worlds;
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
        parentWorldPosition.set(player.getPosition());

        GameManager.transitionScreen(parent, interior, () -> {
            interior.loadWorldTiledMap(false);
            interior.enter();
        });
    }

    /**
     * Transfer to another world
     *
     * @param player      player
     * @param worldIdInto the world ID of the world entering
     */
    public GameWorld transferTo(PlayerSP player, int worldIdInto) {
        final GameWorld to = getWorld(worldIdInto);
        if (to == null) {
            GameLogging.error(this, "Failed to find the world %d", worldIdInto);
            return null;
        }

        GameManager.transitionScreen(player.getWorldState(), to, () -> {
            player.getWorldState().exit();
            player.removeFromWorld();

            to.loadWorldTiledMap(false);
            to.enter();
        });
        return to;
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

        player.createCircleBody(parent.boxWorld(), false);
        player.setPosition(parentWorldPosition);
        player.updateWorldState(parent);

        GameManager.game().getMultiplexer().addProcessor(parent);

        parent.updateRendererMap();
        parent.enter();
    }

    @Override
    public void dispose() {
        worlds.values().forEach(GameWorld::dispose);
        worlds.clear();
    }
}
