package me.vrekt.oasis.world.instance;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents an instance within a world - a dungeon or interior.
 */
public abstract class OasisWorldInstance extends OasisWorld implements Disposable {

    protected final OasisWorld worldIn;
    protected final String instanceName;

    protected final Rectangle exit = new Rectangle();

    public OasisWorldInstance(OasisGame game, OasisPlayerSP player, World world, OasisWorld worldIn, String instanceName) {
        super(game, player, world);
        this.worldIn = worldIn;
        this.instanceName = instanceName;

        getConfiguration().stepTime = 1 / 240f;
    }

    @Override
    public void render(float delta) {
        preRender(delta);
    }

    @Override
    public void renderWorld(SpriteBatch batch, float delta) {
        super.renderWorld(batch, delta);
        endRender();
    }

    /**
     * Enter this instance.
     *
     * @param setScreen if set screen should be used after loading this instance
     */
    public void enter(boolean setScreen) {
        loadInstance(game.getAsset().getWorldMap(instanceName), OasisGameSettings.SCALE);
        isWorldLoaded = true;

        game.getMultiplexer().removeProcessor(worldIn);
        if (setScreen) game.setScreen(this);
    }

    /**
     * Exit this instance
     */
    protected void exit() {

    }

    /**
     * Load this instance
     *
     * @param map        the map
     * @param worldScale the scaling
     */
    private void loadInstance(TiledMap map, float worldScale) {
        if (isWorldLoaded) {
            // indicates this instance is already loaded into memory.
            renderer.setTiledMap(map, spawn.x, spawn.y);
            spawnPlayerInInstance();
            return;
        }

        TiledMapLoader.loadMapCollision(map, worldScale, world, worldIn);
        TiledMapLoader.loadMapActions(map, worldScale, spawn, exit);
        loadWorldObjects(map, game.getAsset(), worldScale);

        renderer.setTiledMap(map, spawn.x, spawn.y);
        game.getMultiplexer().addProcessor(this);

        if (player.isInWorld()) {
            Logging.info(this, "Removing local player from parent world.");
            player.removeFromWorld(player.getGameWorldIn());
        }

        spawnPlayerInInstance();
        addDefaultWorldSystems();

        Logging.info(this, "Loaded instance successfully.");
    }

    private void spawnPlayerInInstance() {
        player.spawnInWorld(this, spawn);
        player.setInInstance(true);
        player.setInstanceIn(this);
    }

}
