package me.vrekt.oasis.world.management;

import com.badlogic.ashley.utils.Bag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntMap;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.entity.player.sp.PlayerSP;
import me.vrekt.oasis.utility.logging.GameLogging;
import me.vrekt.oasis.world.GameWorld;
import me.vrekt.oasis.world.GameWorldInterior;

/**
 * Handles loading and unloading worlds.
 */
public final class WorldManager implements Disposable {

    private final IntMap<GameWorld> worlds = new IntMap<>();
    // the position where the player entered an interior
    private final Vector2 parentWorldPosition = new Vector2();

    // the active world the player is in
    private GameWorld activeWorld;
    // ticking worlds
    private final Bag<GameWorld> tickingWorlds = new Bag<>();

    /**
     * Add a world to the list of worlds
     *
     * @param world world
     */
    public void addWorld(GameWorld world) {
        worlds.put(world.worldId(), world);
    }

    /**
     * Get a world.
     *
     * @param worldId the ID
     * @return the world {@code null} if not found
     */
    public GameWorld getWorld(int worldId) {
        return worlds.get(worldId);
    }

    /**
     * Check if a world exists.
     *
     * @param worldId the ID
     * @return {@code true} if so
     */
    public boolean doesWorldExist(int worldId) {
        return worlds.containsKey(worldId);
    }

    /**
     * @return a list of all worlds linked to by their ID.
     */
    public IntMap<GameWorld> worlds() {
        return worlds;
    }

    /**
     * Will store the position of the player before they enter an interior
     *
     * @param position position
     */
    public void setParentWorldPosition(Vector2 position) {
        this.parentWorldPosition.set(position);
    }

    /**
     * @return the position the player was when they entered an interior.
     */
    public Vector2 parentWorldPosition() {
        return parentWorldPosition;
    }

    /**
     * @return current active world.
     */
    public GameWorld activeWorld() {
        return activeWorld;
    }

    /**
     * Set the active world to tick
     *
     * @param activeWorld active world.
     */
    public void setActiveWorld(GameWorld activeWorld) {
        this.activeWorld = activeWorld;

        // ensure renderer is set correctly.
        activeWorld.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Will update all ticking worlds and render the active world
     *
     * @param delta delta
     */
    public void update(float delta) {
        if (activeWorld != null) activeWorld.render(delta);
        for (int i = 0; i < tickingWorlds.size(); i++) {
            tickingWorlds.get(i).tickWorld(delta);
        }
    }

    /**
     * Call to resize the active world.
     *
     * @param width  new width
     * @param height new height
     */
    public void resizeActiveWorld(int width, int height) {
        if (activeWorld != null) activeWorld.resize(width, height);
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

        GameManager.transitionWorlds(parent, interior, () -> {
            interior.loadWorldTiledMap(false);
            interior.enterWorld();
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

        GameManager.transitionWorlds(player.getWorldState(), to, () -> {
            player.getWorldState().exit();
            player.removeFromWorld();

            player.getTransformComponent().position.set(to.worldOrigin());

            to.loadWorldTiledMap(false);
            to.enterWorld();
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
        // the transform to set when entering the new world.
        player.getTransformComponent().position.set(parentWorldPosition);
        parent.enterWorld();
    }

    @Override
    public void dispose() {
        worlds.values().forEach(GameWorld::dispose);
        worlds.clear();
    }
}
