package me.vrekt.oasis.world.instance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.world.WorldConfiguration;
import lunar.shared.entity.LunarEntity;
import me.vrekt.oasis.GameManager;
import me.vrekt.oasis.OasisGame;
import me.vrekt.oasis.asset.settings.OasisGameSettings;
import me.vrekt.oasis.entity.npc.EntityInteractable;
import me.vrekt.oasis.entity.npc.system.EntityInteractableAnimationSystem;
import me.vrekt.oasis.entity.npc.system.EntityUpdateSystem;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.OasisTiledRenderer;
import me.vrekt.oasis.graphics.Renderable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents an instance within a world - a dungeon or interior.
 */
public abstract class OasisInstance extends OasisWorld implements Disposable {

    protected final OasisWorld worldIn;
    protected final String instanceName;

    protected final SpriteBatch batch;
    protected final OasisTiledRenderer renderer;
    protected final GameGui gameGui;

    protected final Rectangle exit = new Rectangle();

    // pause state
    protected FrameBuffer fbo;
    protected TextureRegion fboTexture;
    protected boolean paused, hasFbo;

    public OasisInstance(OasisGame game, OasisPlayerSP player, World world, OasisWorld worldIn, String instanceName) {
        super(game, player, world);
        this.worldIn = worldIn;
        this.instanceName = instanceName;
        this.batch = worldIn.getGame().getBatch();
        this.renderer = GameManager.getRenderer();
        this.gameGui = worldIn.getGame().getGui();

        final WorldConfiguration config = new WorldConfiguration();
        config.handlePhysics = true;
        config.updateEntityEngine = true;
        config.updateEntities = true;
        config.updateNetworkPlayers = true;
        config.updateLocalPlayer = true;
        config.stepTime = 1 / 240f;
        setConfiguration(config);
    }

    @Override
    public void render(float delta) {
        if (paused && !hasFbo) fbo.begin();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (paused && !hasFbo) {
            // capture fbo.
            renderer.getViewport().apply();
            renderInternal(delta);
            hasFbo = true;
        } else if (paused) {
            // draw fbo.

            gameGui.getStage().getViewport().apply();
            batch.setProjectionMatrix(worldIn.getGame().getGui().getCamera().combined);
            batch.begin();

            if (fboTexture == null) {
                fboTexture = new TextureRegion(fbo.getColorBufferTexture());
                fboTexture.flip(false, true);
            } else if (fboTexture.getTexture() == null) {
                fboTexture.setTexture(fbo.getColorBufferTexture());
            }

            // draw FBO
            batch.draw(fboTexture, 0, 0);
        } else {
            if (fboTexture != null) fboTexture.setTexture(null);
            renderer.getViewport().apply();
            // no pause state, render normally.
            renderInternal(delta);
        }

        // end
        if (paused && hasFbo) fbo.end();
        if (paused && batch.isDrawing()) batch.end();
    }

    protected void renderInternal(float delta) {
        this.update(delta);
        this.renderInstance(delta);
    }

    /**
     * Render this instance
     *
     * @param delta delta time
     */
    protected void renderInstance(float delta) {
        renderer.beginRendering();
        renderer.render();

        // render MP players first,
        for (OasisNetworkPlayer player : players.values()) {
            if (player.isInView(renderer.getCamera())) {
                player.render(batch, delta);
            }
        }

        for (LunarEntity value : entities.values()) {
            if (value instanceof Renderable) {
                if (((Renderable) value).isInView(renderer.getCamera())) {
                    ((Renderable) value).render(batch, delta);
                }
            }
        }

        // render local player next
        player.render(batch, delta);
        batch.end();

        // render gui
        gameGui.updateAndRender();
    }

    /**
     * Enter this instance.
     */
    public void enter() {
        loadInstance(worldIn.getGame().getAsset().getWorldMap(instanceName), OasisGameSettings.SCALE);
        isWorldLoaded = true;
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
            GameManager.getRenderer().setTiledMap(map, spawn.x, spawn.y);
            spawnPlayerInInstance();
            return;
        }
        TiledMapLoader.loadMapCollision(map, worldScale, world, worldIn);
        TiledMapLoader.loadMapActions(map, worldScale, spawn, exit);
        GameManager.getRenderer().setTiledMap(map, spawn.x, spawn.y);

        if (player.isInWorld()) {
            Logging.info(this, "Removing local player from parent world.");
            player.removeEntityInWorld(player.getGameWorldIn());
        }

        // initialize player in this world.
        spawnPlayerInInstance();

        engine.addSystem(new EntityInteractableAnimationSystem(engine));
        engine.addSystem(new EntityUpdateSystem(game, this));

        Logging.info(this, "Loaded instance successfully.");
    }

    private void spawnPlayerInInstance() {
        player.spawnEntityInWorld(this, spawn.x, spawn.y);
        player.setInInstance(true);
        player.setInstanceIn(this);
    }

    /**
     * Add an interactable entity to this world
     * Automatically assigns ID and to engine
     *
     * @param entity the entity
     */
    public void addInteractableEntity(EntityInteractable entity) {
        entity.setEntityId(this.entities.size() + 1);
        this.entities.put(entity.getEntityId(), entity);
        engine.addEntity(entity.getEntity());
    }

    /**
     * Remove an interactable entity from this world
     * TODO: In update system maybe check for a state within the entity to queue to remove from world
     *
     * @param entity the entity
     */
    public void removeInteractableEntity(EntityInteractable entity) {
        this.entities.remove(entity.getEntityId());
        engine.removeEntity(entity.getEntity());
    }

    public void processLeftClickDown() {

    }

    @Override
    public void spawnEntityInWorld(LunarEntity entity, float x, float y) {
        if (entity instanceof OasisPlayerSP) return;
        super.spawnEntityInWorld(entity, x, y);
    }

    @Override
    public void dispose() {
        fbo.dispose();
        super.dispose();
    }
}
