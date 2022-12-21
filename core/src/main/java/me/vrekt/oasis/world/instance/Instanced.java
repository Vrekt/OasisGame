package me.vrekt.oasis.world.instance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import gdx.lunar.world.LunarWorld;
import lunar.shared.entity.player.LunarEntity;
import me.vrekt.oasis.entity.Entity;
import me.vrekt.oasis.entity.player.mp.OasisNetworkPlayer;
import me.vrekt.oasis.entity.player.sp.OasisPlayerSP;
import me.vrekt.oasis.graphics.OasisTiledRenderer;
import me.vrekt.oasis.graphics.Renderable;
import me.vrekt.oasis.gui.GameGui;
import me.vrekt.oasis.utility.logging.Logging;
import me.vrekt.oasis.utility.tiled.TiledMapLoader;
import me.vrekt.oasis.world.OasisWorld;

/**
 * Represents an instance within a world, a dungeon or interior.
 */
public abstract class Instanced extends LunarWorld<OasisPlayerSP, OasisNetworkPlayer, Entity> implements Disposable {

    private final OasisWorld worldIn;
    private final String mapName;

    private final SpriteBatch batch;
    private final OasisTiledRenderer renderer;
    private final GameGui gameGui;

    protected final Vector2 spawn = new Vector2();
    protected final Rectangle exitArea = new Rectangle();

    // pause state
    protected FrameBuffer fbo;
    protected TextureRegion fboTexture;
    protected boolean paused, hasFbo;

    public Instanced(OasisPlayerSP player, World world, OasisWorld worldIn, String mapName) {
        super(player, world);
        this.worldIn = worldIn;
        this.mapName = mapName;
        this.batch = worldIn.getGame().getBatch();
        this.renderer = worldIn.getGame().getRenderer();
        this.gameGui = worldIn.getGame().getGui();

        configuration.stepTime = 1 / 240f;
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

            gameGui.applyStageViewport();
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
        this.renderWorld(delta);
    }

    protected void renderWorld(float delta) {
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
        gameGui.applyStageViewport();
        gameGui.render();
    }

    public void enter() {
        loadInstance(worldIn.getGame().getAsset().getWorldMap(mapName), worldIn.getConfiguration().worldScale);
    }

    public void loadInstance(TiledMap map, float worldScale) {
        TiledMapLoader.loadMapCollision(map, worldScale, world);
        TiledMapLoader.loadMapActions(map, worldScale, spawn, exitArea);

        this.worldIn.getGame().getRenderer().setTiledMap(map, spawn.x, spawn.y);
        // TODO: engine.addSystem(new EntityUpdateSystem(worldIn.getGame(), this));

        if (player.isInWorld()) {
            Logging.info(this, "Removing local player from world.");
            player.removeEntityInWorld(player.getWorldIn());
            player.setGameWorldIn(null);
        }

        // initialize player in this world.
        player.spawnEntityInWorld(this, spawn.x, spawn.y);
        player.setInInstance(true);
        player.setInstanceIn(this);
    }

    @Override
    public void spawnEntityInWorld(LunarEntity entity, float x, float y) {
        if (entity instanceof OasisPlayerSP) return;
        super.spawnEntityInWorld(entity, x, y);
    }

    @Override
    public void dispose() {
        fbo.dispose();
    }
}
